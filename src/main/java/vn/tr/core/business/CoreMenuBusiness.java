package vn.tr.core.business;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.exception.base.BaseException;
import vn.tr.common.core.exception.base.EntityNotFoundException;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class CoreMenuBusiness {
	
	private final CoreMenuService coreMenuService;
	private final CoreMenuMapper coreMenuMapper;
	private final ObjectMapper objectMapper;
	private final CorePermissionService corePermissionService;
	
	@Transactional(readOnly = true)
	public List<CoreMenuData> getMenuTreeForApp(String appCode) {
		List<CoreMenu> flatList = coreMenuService.findAllByAppCode(appCode);
		List<CoreMenuData> dtoList = flatList.stream()
				.map(coreMenuMapper::toData)
				.collect(Collectors.toList());
		
		return buildTree(dtoList);
	}
	
	public void bulkDelete(Collection<Long> ids) {
		coreMenuService.deleteByIds(ids);
	}
	
	@Transactional(readOnly = true)
	public PagedResult<CoreMenuData> findAll(CoreMenuSearchCriteria criteria) {
		Pageable pageable = CoreUtils.getPageRequest(criteria.getPage(), criteria.getSize(), criteria.getSortBy(), criteria.getSortDir());
		Page<CoreMenu> pageCoreMenu = coreMenuService.findAll(criteria, pageable);
		return PagedResult.from(pageCoreMenu, coreMenuMapper::toData);
	}
	
	@Transactional(readOnly = true)
	public List<CoreMenuData> getAll(CoreMenuSearchCriteria criteria) {
		List<CoreMenu> pageCoreMenu = coreMenuService.findAll(criteria);
		return pageCoreMenu.stream().map(coreMenuMapper::toData).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public List<CoreMenuData> getFlatListForApp(String appCode) {
		return coreMenuService.findAllByAppCode(appCode).stream()
				.map(coreMenuMapper::toData)
				.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public CoreMenuData findById(Long id) {
		return coreMenuService.findById(id)
				.map(coreMenuMapper::toData)
				.orElseThrow(() -> new EntityNotFoundException(CoreMenu.class, id));
	}
	
	public CoreMenuData create(CoreMenuData menuData) {
		CoreMenu menu = coreMenuMapper.toEntity(menuData);
		CoreMenu savedMenu = coreMenuService.save(menu);
		return coreMenuMapper.toData(savedMenu);
	}
	
	public CoreMenuData update(Long id, CoreMenuData menuData) {
		CoreMenu menu = coreMenuService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreMenu.class, id));
		
		coreMenuMapper.updateEntityFromData(menuData, menu); // Dùng @MappingTarget
		CoreMenu updatedMenu = coreMenuService.save(menu);
		return coreMenuMapper.toData(updatedMenu);
	}
	
	public void delete(Long id) {
		if (coreMenuService.hasChildren(id)) {
			throw new BaseException("Không thể xóa menu đang có các menu con. Vui lòng xóa các menu con trước.");
		}
		coreMenuService.delete(id);
	}
	
	private List<CoreMenuData> buildTree(List<CoreMenuData> flatList) {
		Map<Long, CoreMenuData> map = flatList.stream()
				.collect(Collectors.toMap(CoreMenuData::getId, Function.identity()));
		
		List<CoreMenuData> roots = new ArrayList<>();
		
		for (CoreMenuData item : flatList) {
			if (item.getParentId() == null) {
				roots.add(item);
			} else {
				CoreMenuData parent = map.get(item.getParentId());
				if (parent != null) {
					if (parent.getChildren() == null) {
						parent.setChildren(new ArrayList<>());
					}
					parent.getChildren().add(item);
				}
			}
		}
		
		// Sắp xếp các con trong mỗi nút theo displayOrder
		roots.forEach(this::sortChildren);
		return roots;
	}
	
	private void sortChildren(CoreMenuData parent) {
		if (parent.getChildren() != null && !parent.getChildren().isEmpty()) {
			parent.getChildren().sort(Comparator.comparing(
					CoreMenuData::getSortOrder,
					Comparator.nullsLast(Integer::compareTo)
			                                              ));
			parent.getChildren().forEach(this::sortChildren);
		}
	}
	
	@Transactional(readOnly = true)
	public List<RouteRecordRawData> getAccessibleRoutesForCurrentUser(String appCode) {
		String username = LoginHelper.getUsername();
		
		Set<String> userPermissionCodes = corePermissionService.findAllCodesByUsernameAndAppCode(username, appCode);
		
		if (corePermissionService.isSuperAdmin(username)) {
			// Super Admin lấy toàn bộ cây
			List<CoreMenu> fullMenuTree = coreMenuService.findAllByAppCode(appCode);
			List<RouteRecordRawData> fullRouteTree = fullMenuTree.stream()
					.map(this::mapMenuEntityToRouteRecord)
					.collect(Collectors.toList());
			return buildRouteTree(fullRouteTree, fullMenuTree);
		}
		
		if (userPermissionCodes.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<CoreMenu> fullMenuTree = coreMenuService.findAllByAppCode(appCode);
		if (fullMenuTree.isEmpty()) {
			return Collections.emptyList();
		}
		
		// Lọc ra các menu mà user có quyền
		List<CoreMenu> accessibleMenus = filterMenuTree(fullMenuTree, userPermissionCodes);
		
		// Chuyển đổi danh sách đã lọc sang DTO RouteRecordRawData
		List<RouteRecordRawData> accessibleRoutes = accessibleMenus.stream()
				.map(this::mapMenuEntityToRouteRecord)
				.collect(Collectors.toList());
		
		// Xây dựng lại cấu trúc cây từ danh sách DTO đã lọc và chuyển đổi
		return buildRouteTree(accessibleRoutes, accessibleMenus);
	}
	
	// --- CÁC HÀM HELPER ĐÃ ĐƯỢC CẬP NHẬT ---
	
	/**
	 * Chuyển đổi một Entity CoreMenu sang DTO RouteRecordRawData.
	 */
	private RouteRecordRawData mapMenuEntityToRouteRecord(CoreMenu menu) {
		RouteRecordRawData route = new RouteRecordRawData();
		route.setName(menu.getCode());
		route.setPath(menu.getPath());
		route.setComponent(menu.getComponent());
		route.setRedirect(menu.getRedirect());
		route.setProps(menu.getProps());
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
	
	/**
	 * Xây dựng cấu trúc cây cho danh sách RouteRecordRawData. Cần thêm tham số originalMenus để lấy parentId.
	 */
	private List<RouteRecordRawData> buildRouteTree(List<RouteRecordRawData> flatRoutes, List<CoreMenu> originalMenus) {
		// Tạo map để tra cứu parentId từ originalMenus
		Map<String, Long> parentIdMap = originalMenus.stream()
				.filter(m -> m.getParentId() != null)
				.collect(Collectors.toMap(CoreMenu::getCode, CoreMenu::getParentId));
		
		Map<Long, String> codeMapById = originalMenus.stream()
				.collect(Collectors.toMap(CoreMenu::getId, CoreMenu::getCode));
		
		Map<String, RouteRecordRawData> mapByCode = flatRoutes.stream()
				.collect(Collectors.toMap(RouteRecordRawData::getName, Function.identity()));
		
		List<RouteRecordRawData> roots = new ArrayList<>();
		
		for (RouteRecordRawData item : flatRoutes) {
			Long parentId = parentIdMap.get(item.getName());
			if (parentId == null) {
				roots.add(item);
			} else {
				String parentCode = codeMapById.get(parentId);
				if (parentCode != null) {
					RouteRecordRawData parent = mapByCode.get(parentCode);
					if (parent != null) {
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<>());
						}
						parent.getChildren().add(item);
					}
				}
			}
		}
		
		// Sắp xếp đệ quy
		roots.forEach(this::sortRouteChildren);
		return roots;
	}
	
	private void sortRouteChildren(RouteRecordRawData parent) {
		if (parent.getChildren() != null && !parent.getChildren().isEmpty()) {
			parent.getChildren().sort(Comparator.comparing(
					route -> (route.getMeta() != null && route.getMeta().getOrder() != null) ? route.getMeta().getOrder() : Integer.MAX_VALUE,
					Comparator.nullsLast(Integer::compareTo)
			                                              ));
			parent.getChildren().forEach(this::sortRouteChildren);
		}
	}
	
	private List<CoreMenu> filterMenuTree(List<CoreMenu> allMenus, Set<String> userPermissions) {
		Map<Long, CoreMenu> menuMapById = allMenus.stream()
				.collect(Collectors.toMap(CoreMenu::getId, Function.identity()));
		
		Set<CoreMenu> accessibleSet = new HashSet<>();
		
		for (CoreMenu menu : allMenus) {
			// Một menu được phép truy cập nếu:
			// 1. Nó không yêu cầu quyền cụ thể (permission_code is null)
			// 2. Hoặc người dùng có quyền đó
			if (menu.getCode() == null || userPermissions.contains(menu.getCode())) {
				// Nếu người dùng có quyền truy cập menu này,
				// chúng ta cần đảm bảo tất cả các menu cha của nó cũng được thêm vào
				// để cấu trúc cây không bị gãy.
				addMenuAndItsParents(menu, menuMapById, accessibleSet);
			}
		}
		return new ArrayList<>(accessibleSet);
	}
	
	private void addMenuAndItsParents(CoreMenu menu, Map<Long, CoreMenu> menuMap, Set<CoreMenu> accessibleSet) {
		CoreMenu current = menu;
		while (current != null && !accessibleSet.contains(current)) {
			accessibleSet.add(current);
			current = (current.getParentId() != null) ? menuMap.get(current.getParentId()) : null;
		}
	}
	
}
