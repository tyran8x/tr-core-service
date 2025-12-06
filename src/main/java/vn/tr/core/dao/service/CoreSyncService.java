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
import vn.tr.core.adapter.LegacyRouteAdapter;
import vn.tr.core.dao.model.CoreMenu;
import vn.tr.core.dao.model.CoreModule;
import vn.tr.core.dao.model.CorePermission;
import vn.tr.core.data.dto.RouteRecordRawData;
import vn.tr.core.data.dto.RouterData;
import vn.tr.core.data.dto.RouterMetaData;

import java.time.LocalDateTime;
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
	private final LegacyRouteAdapter legacyRouteAdapter;
	
	/**
	 * [API CÔNG KHAI DUY NHẤT]
	 * Tự động nhận dạng, chuyển đổi và đồng bộ hóa routes từ Frontend.
	 *
	 * @param appCode        Mã ứng dụng.
	 * @param routesAsObject Dữ liệu routes thô, có thể là định dạng cũ hoặc mới.
	 */
	@Transactional
	public void dispatchAndSyncRoutes(String appCode, Object routesAsObject) {
		log.info("Bắt đầu dispatch và đồng bộ hóa routes cho app: {}", appCode);
		
		List<RouteRecordRawData> standardRoutes;
		if (isLegacyFormat(routesAsObject)) {
			log.info("Phát hiện định dạng router cũ (legacy). Bắt đầu chuyển đổi...");
			List<RouterData> legacyRoutes = objectMapper.convertValue(routesAsObject, new TypeReference<>() {
			});
			standardRoutes = legacyRouteAdapter.transform(legacyRoutes);
		} else {
			log.info("Phát hiện định dạng router chuẩn.");
			standardRoutes = objectMapper.convertValue(routesAsObject, new TypeReference<>() {
			});
		}
		syncStandardRoutes(appCode, standardRoutes);
	}
	
	private boolean isLegacyFormat(Object routesAsObject) {
		if (!(routesAsObject instanceof List) || ((List<?>) routesAsObject).isEmpty()) {
			return false;
		}
		
		Object firstRouteObj = ((List<?>) routesAsObject).getFirst();
		log.info("Checking format for first route: {}", firstRouteObj);
		
		try {
			// 1. Thử chuyển đổi sang DTO chuẩn
			RouteRecordRawData standardDto = objectMapper.convertValue(firstRouteObj, RouteRecordRawData.class);
			
			// 2. Kiểm tra Logic ("Sanity Check")
			// Đặc điểm nhận dạng Vue 2 (Legacy): component là một Map phức tạp (chứa _compiled, __file...)
			// Đặc điểm nhận dạng Chuẩn mới: component thường là String (hoặc null cho route cha)
			
			Object component = standardDto.getComponent();
			if (component instanceof Map<?, ?> compMap) {
				if (compMap.containsKey("_compiled") || compMap.containsKey("__file")) {
					log.info("Phát hiện component là Vue Object phức tạp -> Đây là định dạng LEGACY.");
					return true;
				}
			}
			
			// Kiểm tra thêm: DTO chuẩn thường không có các trường như 'alwaysShow' ở cấp cao nhất
			// (Jackson đã bỏ qua chúng, nhưng nếu chúng ta convert ngược lại từ Map nguồn...)
			// Cách tốt nhất là kiểm tra trực tiếp trên Map nguồn:
			if (firstRouteObj instanceof Map<?, ?> sourceMap) {
				if (sourceMap.containsKey("alwaysShow") || sourceMap.containsKey("hidden")) {
					log.info("Phát hiện trường 'alwaysShow'/'hidden' ở cấp cao nhất -> Đây là định dạng LEGACY.");
					return true;
				}
			}
			
			log.info("Cấu trúc dữ liệu khớp với định dạng CHUẨN.");
			return false; // Là chuẩn
			
		} catch (IllegalArgumentException e) {
			log.info("Convert thất bại. Giả định là định dạng LEGACY. Lỗi: {}", e.getMessage());
			return true;
		}
	}
	
	/**
	 * [LOGIC LÕI - PRIVATE] Thực hiện đồng bộ hóa từ định dạng chuẩn.
	 */
	private void syncStandardRoutes(String appCode, List<RouteRecordRawData> standardRoutes) {
		long start = System.currentTimeMillis();
		if (standardRoutes.isEmpty()) {
			log.info("Danh sách routes chuẩn trống cho app '{}'. Xóa tất cả menu liên quan.", appCode);
			coreMenuService.softDeleteAllByAppCode(appCode);
			return;
		}
		
		Map<String, CoreModule> existingModulesMap = coreModuleService.findAllByAppCode(appCode).stream()
				.collect(Collectors.toMap(CoreModule::getCode, Function.identity()));
		Map<String, CorePermission> existingPermissionsMap = corePermissionService.findAllByAppCode(appCode).stream()
				.collect(Collectors.toMap(CorePermission::getCode, Function.identity()));
		Map<String, CoreMenu> existingMenusMap = coreMenuService.findAllByAppCodeIncludingDeleted(appCode).stream()
				.collect(Collectors.toMap(CoreMenu::getCode, Function.identity()));
		
		syncModules(standardRoutes, appCode, existingModulesMap);
		syncPermissions(standardRoutes, appCode, existingPermissionsMap, existingModulesMap);
		
		List<CoreMenu> allMenusToUpsert = new ArrayList<>();
		Set<String> activeMenuCodes = new HashSet<>();
		Map<String, String> parentChildMap = new HashMap<>();
		
		// Pass 1: Tạo/Cập nhật thực thể
		
		processRoutesRecursively(standardRoutes, null, appCode, existingMenusMap, allMenusToUpsert, activeMenuCodes, parentChildMap);
		log.info("allMenusToUpsert: {}", allMenusToUpsert.size());
		coreMenuService.saveAll(allMenusToUpsert);
		
		// Pass 2: Cập nhật parentId
		Map<String, CoreMenu> savedMenuMap = allMenusToUpsert.stream().collect(Collectors.toMap(CoreMenu::getCode, Function.identity()));
		existingMenusMap.forEach(savedMenuMap::putIfAbsent);
		
		List<CoreMenu> menusToUpdateParent = new ArrayList<>();
		updateParentIdsRecursivelyPass2(standardRoutes, null, savedMenuMap, menusToUpdateParent);
		if (!menusToUpdateParent.isEmpty()) {
			log.info("allMenusToUpsert: {}", menusToUpdateParent.size());
			coreMenuService.saveAll(menusToUpdateParent);
		}
		
		// Pass 3: Xóa mềm
		List<CoreMenu> menusToDelete = new ArrayList<>();
		for (CoreMenu existingMenu : existingMenusMap.values()) {
			if (existingMenu.getDeletedAt() == null && !activeMenuCodes.contains(existingMenu.getCode())) {
				existingMenu.setDeletedAt(LocalDateTime.now());
				menusToDelete.add(existingMenu);
			}
		}
		log.info("menusToDelete: {}", menusToDelete.size());
		if (!menusToDelete.isEmpty()) {
			coreMenuService.saveAll(menusToDelete);
			log.info("Đã xóa mềm {} menu không còn tồn tại cho app '{}'.", menusToDelete.size(), appCode);
		}
		log.info("Hoàn thành đồng bộ hóa lõi cho app '{}'. Tổng thời gian: {}ms", appCode, System.currentTimeMillis() - start);
	}
	
	private void processRoutesRecursively(List<RouteRecordRawData> routes, @Nullable String parentCode, String appCode,
			Map<String, CoreMenu> existingMenusMap, List<CoreMenu> allMenusToUpsert, Set<String> activeMenuCodes,
			Map<String, String> parentChildMap) {
		int order = 0;
		for (RouteRecordRawData routeData : routes) {
			String menuCode = routeData.getName();
			if (StringUtils.isBlank(menuCode)) continue;
			
			order++;
			activeMenuCodes.add(menuCode);
			if (parentCode != null) {
				parentChildMap.put(menuCode, parentCode);
			}
			
			CoreMenu menu = existingMenusMap.getOrDefault(menuCode, new CoreMenu());
			mapRouteToMenuEntity(menu, routeData, order, appCode);
			allMenusToUpsert.add(menu);
			
			if (routeData.getChildren() != null && !routeData.getChildren().isEmpty()) {
				processRoutesRecursively(routeData.getChildren(), menuCode, appCode, existingMenusMap, allMenusToUpsert, activeMenuCodes,
						parentChildMap);
			}
		}
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
	
	/**
	 * Pass 2: Duyệt lại cây và cập nhật parentId chính xác sau khi tất cả menu đã có ID.
	 */
	private void updateParentIdsRecursivelyPass2(List<RouteRecordRawData> routes, @Nullable CoreMenu parent,
			Map<String, CoreMenu> savedMenuMap, List<CoreMenu> menusToUpdateParent) {
		for (RouteRecordRawData routeData : routes) {
			String menuCode = routeData.getName();
			if (StringUtils.isBlank(menuCode)) continue;
			
			CoreMenu currentMenu = savedMenuMap.get(menuCode);
			
			if (currentMenu == null) continue;
			
			Long parentId = (parent != null) ? parent.getId() : null;
			if (!Objects.equals(currentMenu.getParentId(), parentId)) {
				currentMenu.setParentId(parentId);
				menusToUpdateParent.add(currentMenu);
			}
			
			if (routeData.getChildren() != null && !routeData.getChildren().isEmpty()) {
				updateParentIdsRecursivelyPass2(routeData.getChildren(), currentMenu, savedMenuMap, menusToUpdateParent);
			}
		}
	}
	
	/**
	 * **SỬA LỖI TẠI ĐÂY: Xóa tham số parentId không cần thiết**
	 * Map dữ liệu từ DTO của route sang thực thể CoreMenu.
	 *
	 * @param menu      Thực thể CoreMenu để điền dữ liệu vào.
	 * @param routeData DTO chứa dữ liệu từ Frontend.
	 * @param order     Thứ tự của menu trong cấp của nó.
	 * @param appCode   Mã ứng dụng.
	 */
	private void mapRouteToMenuEntity(CoreMenu menu, RouteRecordRawData routeData, int order, String appCode) {
		menu.setAppCode(appCode);
		menu.setCode(routeData.getName());
		menu.setPath(routeData.getPath());
		menu.setSortOrder(order);
		menu.setDeletedAt(null); // Kích hoạt lại nếu nó đã bị xóa mềm
		
		// Xử lý các trường từ Vue 2/legacy format
		menu.setRedirect(routeData.getRedirect());
		//menu.setIsAlwaysShow(Boolean.TRUE.equals(routeData.getAlwaysShow()));
		
		// Xử lý component với logic ưu tiên
		String finalComponent = "Layout"; // Mặc định
		if (Objects.nonNull(routeData.getComponent())) {
			finalComponent = routeData.getComponent().toString();
		}
		menu.setComponent(finalComponent);
		
		// Xử lý props
		if (routeData.getProps() != null) {
			try {
				menu.setProps(objectMapper.writeValueAsString(routeData.getProps()));
			} catch (JsonProcessingException e) {
				log.error("Lỗi khi serialize props cho menu '{}': {}", menu.getCode(), e.getMessage());
				menu.setProps(null);
			}
		} else {
			menu.setProps(null);
		}
		
		// Xử lý meta
		if (routeData.getMeta() != null) {
			RouterMetaData meta = routeData.getMeta();
			menu.setName(meta.getTitle());
			menu.setIcon(meta.getIcon());
			menu.setIsHidden(Boolean.TRUE.equals(meta.getHideInMenu()));
			menu.setIsNoCache(Boolean.TRUE.equals(meta.getNoCache()));
			menu.setIsAffix(Boolean.TRUE.equals(meta.getAffixTab()));
			menu.setIsBreadcrumb(meta.getHideInBreadcrumb());
			menu.setActiveMenu(meta.getActiveMenu());
			menu.setLink(meta.getLink());
			try {
				menu.setExtraMeta(objectMapper.writeValueAsString(routeData.getMeta()));
			} catch (JsonProcessingException e) {
				log.error("Lỗi khi serialize getMeta cho menu '{}': {}", menu.getCode(), e.getMessage());
				menu.setExtraMeta(null);
			}
		} else {
			menu.setName(routeData.getName());
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
