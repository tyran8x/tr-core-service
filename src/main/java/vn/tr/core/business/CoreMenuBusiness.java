package vn.tr.core.business;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.exception.base.DataConstraintViolationException;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.core.exception.base.PermissionDeniedException;
import vn.tr.common.core.utils.StringUtils;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.common.web.utils.PagedResult;
import vn.tr.core.dao.model.CoreMenu;
import vn.tr.core.dao.service.CoreMenuService;
import vn.tr.core.dao.service.CorePermissionService;
import vn.tr.core.data.criteria.CoreMenuSearchCriteria;
import vn.tr.core.data.dto.CoreMenuData;
import vn.tr.core.data.dto.RouteRecordRawData;
import vn.tr.core.data.dto.RouterMetaData;
import vn.tr.core.data.mapper.CoreMenuMapper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Lớp Business (Facade) điều phối các nghiệp vụ phức tạp liên quan đến Quản lý Menu (CoreMenu).
 *
 * @author tyran8x
 * @version 2.2 (Refactored Tree & Permission Logic)
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CoreMenuBusiness {
	
	private final CoreMenuService coreMenuService;
	private final CoreMenuMapper coreMenuMapper;
	private final ObjectMapper objectMapper;
	private final CorePermissionService corePermissionService;
	
	//<editor-fold desc="Standard CRUD Operations">
	
	/**
	 * Tạo mới một menu.
	 *
	 * @param menuData       Dữ liệu của menu cần tạo.
	 * @param appCodeContext Ngữ cảnh ứng dụng.
	 *
	 * @return Dữ liệu của menu sau khi tạo.
	 */
	public CoreMenuData create(CoreMenuData menuData, String appCodeContext) {
		menuData.setAppCode(appCodeContext);
		CoreMenu menu = coreMenuMapper.toEntity(menuData);
		CoreMenu savedMenu = coreMenuService.save(menu);
		return coreMenuMapper.toData(savedMenu);
	}
	
	/**
	 * Cập nhật thông tin một menu.
	 *
	 * @param id             ID của menu cần cập nhật.
	 * @param menuData       Dữ liệu mới.
	 * @param appCodeContext Ngữ cảnh ứng dụng để kiểm tra quyền.
	 *
	 * @return Dữ liệu của menu sau khi cập nhật.
	 */
	public CoreMenuData update(Long id, CoreMenuData menuData, String appCodeContext) {
		CoreMenu menu = coreMenuService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreMenu.class, id));
		if (!Objects.equals(menu.getAppCode(), appCodeContext)) {
			throw new PermissionDeniedException("Không có quyền cập nhật menu thuộc ứng dụng khác.");
		}
		
		coreMenuMapper.updateEntityFromData(menuData, menu);
		CoreMenu updatedMenu = coreMenuService.save(menu);
		return coreMenuMapper.toData(updatedMenu);
	}
	
	/**
	 * Xóa một menu (xóa mềm).
	 *
	 * @param id             ID của menu cần xóa.
	 * @param appCodeContext Ngữ cảnh ứng dụng để kiểm tra quyền.
	 */
	public void delete(Long id, String appCodeContext) {
		CoreMenu menu = coreMenuService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreMenu.class, id));
		if (!Objects.equals(menu.getAppCode(), appCodeContext)) {
			throw new PermissionDeniedException("Không có quyền xóa menu thuộc ứng dụng khác.");
		}
		
		if (coreMenuService.hasChildren(id)) {
			throw new DataConstraintViolationException("Không thể xóa menu đang có các menu con. Vui lòng xóa các menu con trước.");
		}
		coreMenuService.deleteByIds(List.of(id));
	}
	
	/**
	 * Xóa hàng loạt menu.
	 *
	 * @param ids            Collection các ID cần xóa.
	 * @param appCodeContext Ngữ cảnh ứng dụng.
	 */
	public void bulkDelete(Collection<Long> ids, String appCodeContext) {
		// Cần thêm logic kiểm tra quyền cho từng ID trước khi xóa
		coreMenuService.deleteByIds(ids);
	}
	
	/**
	 * Lấy thông tin chi tiết một menu.
	 */
	@Transactional(readOnly = true)
	public CoreMenuData findById(Long id, String appCodeContext) {
		CoreMenu menu = coreMenuService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreMenu.class, id));
		if (!Objects.equals(menu.getAppCode(), appCodeContext)) {
			throw new PermissionDeniedException("Không có quyền xem menu thuộc ứng dụng khác.");
		}
		return coreMenuMapper.toData(menu);
	}
	
	/**
	 * Tìm kiếm và phân trang menu.
	 */
	@Transactional(readOnly = true)
	public PagedResult<CoreMenuData> findAll(CoreMenuSearchCriteria criteria, String appCodeContext) {
		criteria.setAppCode(appCodeContext);
		Pageable pageable = CoreUtils.getPageRequest(criteria);
		Page<CoreMenu> pageCoreMenu = coreMenuService.findAll(criteria, pageable);
		return PagedResult.from(pageCoreMenu, coreMenuMapper::toData);
	}
	
	//</editor-fold>
	
	//<editor-fold desc="Tree and Route Operations">
	
	/**
	 * Lấy toàn bộ cây menu cho một ứng dụng. Hữu ích cho màn hình quản lý menu.
	 *
	 * @param appCode Mã ứng dụng.
	 *
	 * @return Danh sách các menu gốc của cây.
	 */
	@Transactional(readOnly = true)
	public List<CoreMenuData> getMenuTreeForApp(String appCode) {
		List<CoreMenu> flatList = coreMenuService.findAllByAppCode(appCode);
		List<CoreMenuData> dtoList = coreMenuMapper.toData(flatList);
		return buildMenuDataTree(dtoList);
	}
	
	/**
	 * Lấy cây routes mà người dùng hiện tại có quyền truy cập.
	 * Đây là nghiệp vụ cốt lõi để xây dựng menu động trên Frontend.
	 *
	 * @param appCode Ngữ cảnh ứng dụng.
	 *
	 * @return Cây routes đã được lọc theo quyền và định dạng cho Vue Router.
	 */
	@Transactional(readOnly = true)
	public List<RouteRecordRawData> getAccessibleRoutesForCurrentUser(String appCode) {
		String username = LoginHelper.getUsername();
		List<CoreMenu> accessibleMenus;
		
		List<CoreMenu> allMenusInApp = coreMenuService.findAllByAppCode(appCode);
		if (allMenusInApp.isEmpty()) {
			return Collections.emptyList();
		}
		
		if (corePermissionService.isSuperAdmin(username)) {
			accessibleMenus = allMenusInApp;
		} else {
			Set<String> userPermissions = corePermissionService.findAllCodesByUsernameAndAppCode(username, appCode);
			if (userPermissions.isEmpty()) {
				return Collections.emptyList();
			}
			accessibleMenus = filterAccessibleMenus(allMenusInApp, userPermissions);
		}
		
		if (accessibleMenus.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<RouteRecordRawData> accessibleRoutes = accessibleMenus.stream()
				.map(this::mapMenuEntityToRouteRecord)
				.collect(Collectors.toList());
		
		return buildRouteRecordTree(accessibleRoutes, accessibleMenus);
	}
	
	//</editor-fold>
	
	//<editor-fold desc="Private Helper Methods">
	
	private List<CoreMenuData> buildMenuDataTree(List<CoreMenuData> flatList) {
		Map<Long, CoreMenuData> map = flatList.stream().collect(Collectors.toMap(CoreMenuData::getId, Function.identity()));
		List<CoreMenuData> roots = new ArrayList<>();
		
		for (CoreMenuData item : flatList) {
			if (item.getParentId() == null) {
				roots.add(item);
			} else {
				CoreMenuData parent = map.get(item.getParentId());
				if (parent != null) {
					parent.getChildren().add(item);
				}
			}
		}
		sortMenuDataChildrenRecursively(roots);
		return roots;
	}
	
	private void sortMenuDataChildrenRecursively(List<CoreMenuData> nodes) {
		if (nodes == null || nodes.isEmpty()) return;
		nodes.sort(Comparator.comparing(CoreMenuData::getSortOrder, Comparator.nullsLast(Integer::compareTo)));
		for (CoreMenuData node : nodes) {
			sortMenuDataChildrenRecursively(node.getChildren());
		}
	}
	
	private List<CoreMenu> filterAccessibleMenus(List<CoreMenu> allMenus, Set<String> userPermissions) {
		Map<Long, CoreMenu> menuMapById = allMenus.stream().collect(Collectors.toMap(CoreMenu::getId, Function.identity()));
		Set<CoreMenu> accessibleSet = new HashSet<>();
		
		for (CoreMenu menu : allMenus) {
			if (userPermissions.contains(menu.getCode())) {
				addMenuAndItsParents(menu, menuMapById, accessibleSet);
			}
		}
		return new ArrayList<>(accessibleSet);
	}
	
	private void addMenuAndItsParents(CoreMenu menu, Map<Long, CoreMenu> menuMap, Set<CoreMenu> accessibleSet) {
		CoreMenu current = menu;
		while (current != null && accessibleSet.add(current)) {
			current = (current.getParentId() != null) ? menuMap.get(current.getParentId()) : null;
		}
	}
	
	private List<RouteRecordRawData> buildRouteRecordTree(List<RouteRecordRawData> flatRoutes, List<CoreMenu> originalMenus) {
		Map<String, CoreMenu> menuMapByCode = originalMenus.stream().collect(Collectors.toMap(CoreMenu::getCode, Function.identity()));
		Map<String, RouteRecordRawData> routeMapByCode = flatRoutes.stream()
				.collect(Collectors.toMap(RouteRecordRawData::getName, Function.identity()));
		List<RouteRecordRawData> roots = new ArrayList<>();
		
		for (RouteRecordRawData item : flatRoutes) {
			CoreMenu originalMenu = menuMapByCode.get(item.getName());
			if (originalMenu == null) continue;
			
			if (originalMenu.getParentId() == null) {
				roots.add(item);
			} else {
				CoreMenu parentMenu = originalMenus.stream().filter(m -> originalMenu.getParentId().equals(m.getId())).findFirst().orElse(null);
				if (parentMenu != null) {
					RouteRecordRawData parentRoute = routeMapByCode.get(parentMenu.getCode());
					if (parentRoute != null) {
						parentRoute.getChildren().add(item);
					}
				}
			}
		}
		sortRouteChildrenRecursively(roots);
		return roots;
	}
	
	private void sortRouteChildrenRecursively(List<RouteRecordRawData> nodes) {
		if (nodes == null || nodes.isEmpty()) return;
		nodes.sort(Comparator.comparing(RouteRecordRawData::getSortOrder, Comparator.nullsLast(Integer::compareTo)));
		for (RouteRecordRawData node : nodes) {
			sortRouteChildrenRecursively(node.getChildren());
		}
	}
	
	private RouteRecordRawData mapMenuEntityToRouteRecord(CoreMenu menu) {
		RouteRecordRawData route = new RouteRecordRawData();
		route.setName(menu.getCode());
		route.setPath(menu.getPath());
		route.setComponent(menu.getComponent());
		route.setRedirect(menu.getRedirect());
		route.setSortOrder(menu.getSortOrder()); // Gán sortOrder
		
		if (StringUtils.isNotBlank(menu.getProps())) {
			try {
				route.setProps(objectMapper.readValue(menu.getProps(), Object.class));
			} catch (JsonProcessingException e) {
				log.warn("Không thể deserialize props cho menu: {}", menu.getCode());
			}
		}
		
		RouterMetaData meta = new RouterMetaData();
		if (menu.getExtraMeta() != null && !menu.getExtraMeta().isBlank()) {
			try {
				meta = objectMapper.readValue(menu.getExtraMeta(), RouterMetaData.class);
			} catch (JsonProcessingException e) {
				log.error("Lỗi deserialize extra_meta cho menu: {}", menu.getCode(), e);
			}
		}
		
		// Ghi đè các giá trị từ cột riêng lẻ (luôn là nguồn tin cậy nhất)
		meta.setTitle(menu.getName());
		meta.setIcon(menu.getIcon());
		meta.setHideInMenu(menu.getIsHidden());
		meta.setOrder(menu.getSortOrder());
		route.setMeta(meta);
		return route;
	}
	
	//</editor-fold>
}
