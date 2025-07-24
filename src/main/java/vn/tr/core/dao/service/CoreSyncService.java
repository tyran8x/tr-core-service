package vn.tr.core.dao.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreMenu;
import vn.tr.core.dao.model.CoreModule;
import vn.tr.core.dao.model.CorePermission;
import vn.tr.core.data.dto.RouteRecordRawData;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service chịu trách nhiệm đồng bộ hóa cấu trúc Menu, Module và Quyền hạn từ một cấu trúc routes được định nghĩa ở Frontend. Dịch vụ này giúp tự động
 * hóa việc quản lý các tài nguyên RBAC cơ bản, giảm thiểu sai sót do con người.
 *
 * @author tyran8x
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CoreSyncService {
	
	private final CoreMenuService coreMenuService;
	private final CorePermissionService corePermissionService;
	private final CoreModuleService coreModuleService;
	private final ObjectMapper objectMapper;
	
	/**
	 * Đồng bộ hóa toàn bộ cấu trúc routes từ Frontend vào database của core-service. Thao tác này sẽ thêm mới/cập nhật các Module, Permission, Menu
	 * và xóa mềm các Menu không còn tồn tại.
	 *
	 * @param appCode        Mã của ứng dụng cần đồng bộ hóa.
	 * @param routesAsObject Dữ liệu routes từ Frontend, thường là một List<Map<String, Object>>.
	 */
	@Transactional
	public void syncRoutesFromFrontend(String appCode, Object routesAsObject) {
		log.info("Bắt đầu đồng bộ hóa routes cho app: {}", appCode);
		long start = System.currentTimeMillis();
		
		List<RouteRecordRawData> routeRecordRaws = objectMapper.convertValue(routesAsObject, new TypeReference<>() {
		});
		if (routeRecordRaws == null || routeRecordRaws.isEmpty()) {
			log.warn("Danh sách routes trống cho app '{}', không có gì để đồng bộ.", appCode);
			// Cân nhắc xóa tất cả menu của app này nếu nghiệp vụ yêu cầu
			return;
		}
		
		// --- BƯỚC 1: Tải trước toàn bộ dữ liệu hiện có để thao tác in-memory, tránh N+1 query ---
		Map<String, CoreModule> existingModulesMap = coreModuleService.findAllByAppCode(appCode).stream()
				.collect(Collectors.toMap(CoreModule::getCode, Function.identity()));
		
		Map<String, CorePermission> existingPermissionsMap = corePermissionService.findAllByAppCode(appCode).stream()
				.collect(Collectors.toMap(CorePermission::getCode, Function.identity()));
		
		Map<String, CoreMenu> existingMenusMap = coreMenuService.findAllByAppCode(appCode).stream()
				.collect(Collectors.toMap(CoreMenu::getCode, Function.identity()));
		
		// --- BƯỚC 2: Đồng bộ Modules và Permissions (thêm mới nếu chưa có) ---
		syncModules(routeRecordRaws, appCode, existingModulesMap);
		syncPermissions(routeRecordRaws, appCode, existingPermissionsMap, existingModulesMap);
		
		// --- BƯỚC 3: Xây dựng và đồng bộ Menu theo cấu trúc cây (Upsert) ---
		List<CoreMenu> allMenusToUpsert = new ArrayList<>();
		Set<String> activeMenuCodes = new HashSet<>();
		upsertMenuHierarchy(routeRecordRaws, null, appCode, existingMenusMap, allMenusToUpsert, activeMenuCodes);
		
		// --- BƯỚC 4: Xác định và xóa mềm các menu không còn tồn tại ---
		Set<String> menuCodesToDelete = existingMenusMap.keySet();
		menuCodesToDelete.removeAll(activeMenuCodes);
		
		if (!menuCodesToDelete.isEmpty()) {
			List<CoreMenu> menusToDelete = menuCodesToDelete.stream()
					.map(existingMenusMap::get)
					.collect(Collectors.toList());
			coreMenuService.softDeleteAll(menusToDelete);
			log.info("Đã xóa mềm {} menu không còn tồn tại cho app '{}'.", menusToDelete.size(), appCode);
		}
		
		// --- BƯỚC 5: Lưu tất cả các thay đổi (thêm mới/cập nhật) cho Menu ---
		if (!allMenusToUpsert.isEmpty()) {
			coreMenuService.saveAll(allMenusToUpsert);
			log.info("Đã lưu {} thay đổi cho menu của app '{}'.", allMenusToUpsert.size(), appCode);
		}
		
		log.info("Hoàn thành đồng bộ hóa routes cho app '{}'. Tổng thời gian: {}ms", appCode, System.currentTimeMillis() - start);
	}
	
	private void syncModules(List<RouteRecordRawData> routes, String appCode, Map<String, CoreModule> existingModulesMap) {
		Set<String> moduleCodesFromFE = routes.stream()
				.map(RouteRecordRawData::getName).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
		
		List<CoreModule> modulesToCreate = moduleCodesFromFE.stream()
				.filter(code -> !existingModulesMap.containsKey(code))
				.map(code -> CoreModule.builder().appCode(appCode).code(code).name(code).build())
				.collect(Collectors.toList());
		
		if (!modulesToCreate.isEmpty()) {
			List<CoreModule> savedModules = coreModuleService.saveAll(modulesToCreate);
			savedModules.forEach(module -> existingModulesMap.put(module.getCode(), module));
			log.info("Đã tự động tạo {} module mới cho app '{}'.", savedModules.size(), appCode);
		}
	}
	
	private void syncPermissions(List<RouteRecordRawData> routes, String appCode, Map<String, CorePermission> existingPermissionsMap,
			Map<String, CoreModule> modulesMap) {
		Set<String> permissionCodesFromFE = new HashSet<>();
		collectAllPermissionCodesRecursively(routes, permissionCodesFromFE);
		
		List<CorePermission> permissionsToCreate = permissionCodesFromFE.stream()
				.filter(code -> !existingPermissionsMap.containsKey(code))
				.map(code -> {
					String moduleCode = extractModuleCodeFromPermissionCode(code).orElse(null);
					CoreModule module = (moduleCode != null) ? modulesMap.get(moduleCode) : null;
					return CorePermission.builder()
							.appCode(appCode)
							.code(code)
							.name(generatePermissionNameFromCode(code))
							.moduleId(module != null ? module.getId() : null)
							.build();
				})
				.collect(Collectors.toList());
		
		if (!permissionsToCreate.isEmpty()) {
			corePermissionService.saveAll(permissionsToCreate);
			log.info("Đã tự động tạo {} permission mới cho app '{}'.", permissionsToCreate.size(), appCode);
		}
	}
	
	private void upsertMenuHierarchy(List<RouteRecordRawData> routeDataList, @Nullable Long parentId, String appCode,
			Map<String, CoreMenu> existingMenusMap, List<CoreMenu> allMenusToUpsert, Set<String> activeMenuCodes) {
		int order = 0;
		for (RouteRecordRawData routeData : routeDataList) {
			String menuCode = routeData.getName();
			if (StringUtils.isBlank(menuCode)) continue;
			
			order++;
			activeMenuCodes.add(menuCode);
			
			CoreMenu menu = existingMenusMap.getOrDefault(menuCode, new CoreMenu());
			mapRouteToMenuEntity(menu, routeData, parentId, order, appCode);
			allMenusToUpsert.add(menu);
			
			if (routeData.getChildren() != null && !routeData.getChildren().isEmpty()) {
				// Để xử lý parentId cho các menu mới tạo, ta cần lưu menu cha trước để lấy ID.
				// Điều này phá vỡ tối ưu "saveAll một lần".
				// Một cách tiếp cận thực dụng là hy sinh một chút hiệu năng để đảm bảo tính đúng đắn.
				CoreMenu savedParent = coreMenuService.save(menu);
				upsertMenuHierarchy(routeData.getChildren(), savedParent.getId(), appCode, existingMenusMap, allMenusToUpsert, activeMenuCodes);
			}
		}
	}
	
	private void mapRouteToMenuEntity(CoreMenu menu, RouteRecordRawData routeData, @Nullable Long parentId, int order, String appCode) {
		menu.setAppCode(appCode);
		menu.setParentId(parentId);
		menu.setCode(routeData.getName());
		menu.setName(routeData.getMeta() != null && StringUtils.isNotBlank(routeData.getMeta().getTitle())
				? routeData.getMeta().getTitle()
				: routeData.getName());
		menu.setPath(routeData.getPath());
		menu.setComponent(routeData.getComponent());
		menu.setSortOrder(order);
		
		if (routeData.getMeta() != null) {
			menu.setIcon(routeData.getMeta().getIcon());
			menu.setIsHidden(Boolean.TRUE.equals(routeData.getMeta().getHideInMenu()));
			try {
				menu.setExtraMeta(objectMapper.writeValueAsString(routeData.getMeta()));
			} catch (JsonProcessingException e) {
				log.error("Lỗi khi serialize meta cho menu '{}': {}", menu.getCode(), e.getMessage());
				menu.setExtraMeta("{}");
			}
		}
	}
	
	private void collectAllPermissionCodesRecursively(List<RouteRecordRawData> routes, Set<String> permissions) {
		for (RouteRecordRawData route : routes) {
			if (StringUtils.isNotBlank(route.getName())) {
				permissions.add(route.getName());
			}
			if (route.getChildren() != null && !route.getChildren().isEmpty()) {
				collectAllPermissionCodesRecursively(route.getChildren(), permissions);
			}
		}
	}
	
	private Optional<String> extractModuleCodeFromPermissionCode(String permissionCode) {
		for (int i = 1; i < permissionCode.length(); i++) {
			char currentChar = permissionCode.charAt(i);
			if (Character.isUpperCase(currentChar) && Character.isLowerCase(permissionCode.charAt(i - 1))) {
				return Optional.of(permissionCode.substring(0, i));
			}
		}
		return Optional.empty();
	}
	
	private String generatePermissionNameFromCode(String code) {
		return code.replaceAll("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])", " ");
	}
}
