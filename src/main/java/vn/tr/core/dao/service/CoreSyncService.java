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
 * Service chịu trách nhiệm đồng bộ hóa cấu trúc Menu và Quyền từ Frontend.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CoreSyncService {
	
	private final CoreMenuService coreMenuService;
	private final CorePermissionService corePermissionService;
	private final CoreModuleService coreModuleService;
	private final ObjectMapper objectMapper;
	
	@Transactional
	public void syncRoutesFromFrontend(String appCode, Object routesAsObject) {
		log.info("Bắt đầu đồng bộ hóa routes cho app: {}", appCode);
		long start = System.currentTimeMillis();
		
		List<RouteRecordRawData> routeRecordRaws = objectMapper.convertValue(routesAsObject, new TypeReference<>() {
		});
		
		if (routeRecordRaws == null || routeRecordRaws.isEmpty()) {
			log.info("Danh sách routes trống, không có gì để đồng bộ.");
			return;
		}
		
		// --- BƯỚC 1: Đánh dấu tất cả menu hiện có là "chờ xóa" ---
		coreMenuService.markAllAsPendingDeletionForApp(appCode);
		
		// --- BƯỚC 2: Thu thập, tạo mới và đồng bộ Modules & Permissions ---
		Set<String> moduleCodesFromFE = extractModuleCodes(routeRecordRaws);
		syncModules(moduleCodesFromFE, appCode);
		
		Set<String> permissionCodesFromFE = new HashSet<>();
		collectAllPermissionCodesRecursively(routeRecordRaws, permissionCodesFromFE);
		syncPermissions(permissionCodesFromFE, appCode);
		
		// --- BƯỚC 3: Xây dựng và lưu lại cấu trúc Menu theo kiểu cây ---
		saveMenuHierarchy(routeRecordRaws, null, appCode);
		
		// --- BƯỚC 4: Dọn dẹp các menu không còn tồn tại ---
		int deletedCount = coreMenuService.deletePendingMenusForApp(appCode);
		log.info("Đã xóa {} menu không còn tồn tại.", deletedCount);
		
		log.info("Hoàn thành đồng bộ hóa routes. Tổng thời gian: {}ms", System.currentTimeMillis() - start);
	}
	
	private void saveMenuHierarchy(List<RouteRecordRawData> routeDataList, @Nullable Long parentId, String appCode) {
		int order = 0;
		for (RouteRecordRawData routeData : routeDataList) {
			if (StringUtils.isBlank(routeData.getName())) {
				continue;
			}
			order++;
			
			CoreMenu menu = coreMenuService.findByCodeSafely(appCode, routeData.getName()).orElseGet(CoreMenu::new);
			
			mapRouteToMenuEntity(menu, routeData, parentId, order, appCode);
			
			CoreMenu savedMenu = coreMenuService.save(menu);
			
			if (routeData.getChildren() != null && !routeData.getChildren().isEmpty()) {
				saveMenuHierarchy(routeData.getChildren(), savedMenu.getId(), appCode);
			}
		}
	}
	
	private void mapRouteToMenuEntity(CoreMenu menu, RouteRecordRawData routeData, @Nullable Long parentId, int order, String appCode) {
		menu.setAppCode(appCode);
		
		// Hoặc không cần làm gì nếu mặc định đã là null
		menu.setParentId(parentId);
		menu.setCode(routeData.getName());
		menu.setName(routeData.getMeta() != null && StringUtils.isNotBlank(routeData.getMeta().getTitle())
				? routeData.getMeta().getTitle()
				: routeData.getName());
		menu.setCode(routeData.getName());
		menu.setPath(routeData.getPath());
		menu.setComponent(routeData.getComponent());
		menu.setSortOrder(order);
		menu.setIsPendingDeletion(false);
		
		if (routeData.getMeta() != null) {
			menu.setIcon(routeData.getMeta().getIcon());
			menu.setIsHidden(Boolean.TRUE.equals(routeData.getMeta().getHideInMenu()));
			try {
				menu.setExtraMeta(objectMapper.writeValueAsString(routeData.getMeta()));
			} catch (JsonProcessingException e) {
				log.error("mapRouteToMenuEntity: {}", e.getMessage());
			}
			
		}
	}
	
	private Set<String> extractModuleCodes(List<RouteRecordRawData> routes) {
		return routes.stream()
				.map(RouteRecordRawData::getName)
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.toSet());
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
	
	private void syncModules(Set<String> moduleCodesFromFE, String appCode) {
		if (moduleCodesFromFE.isEmpty()) return;
		
		Set<String> existingModuleCodes = coreModuleService.findAllCodesByAppCode(appCode);
		moduleCodesFromFE.stream()
				.filter(code -> !existingModuleCodes.contains(code))
				.forEach(code -> coreModuleService.findOrCreate(code, code, appCode));
	}
	
	private void syncPermissions(Set<String> permissionCodesFromFE, String appCode) {
		if (permissionCodesFromFE.isEmpty()) return;
		
		Set<String> existingPermissionCodes = corePermissionService.findAllCodesByAppCode(appCode);
		
		Set<String> newPermissionCodes = permissionCodesFromFE.stream()
				.filter(code -> !existingPermissionCodes.contains(code))
				.collect(Collectors.toSet());
		
		if (!newPermissionCodes.isEmpty()) {
			Map<String, CoreModule> modulesMap = coreModuleService.findAllByAppCode(appCode).stream()
					.collect(Collectors.toMap(CoreModule::getCode, Function.identity()));
			
			List<CorePermission> permissionsToCreate = newPermissionCodes.stream()
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
			corePermissionService.saveAll(permissionsToCreate);
			log.info("Đã tự động tạo {} permission mới cho app '{}'.", newPermissionCodes.size(), appCode);
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
