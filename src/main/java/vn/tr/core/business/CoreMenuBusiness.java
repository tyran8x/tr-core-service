package vn.tr.core.business;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import vn.tr.common.core.domain.model.LoginUser;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.core.utils.FunctionUtils;
import vn.tr.common.json.utils.JsonUtils;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.core.dao.model.CoreMenu;
import vn.tr.core.dao.service.CoreMenuService;
import vn.tr.core.dao.service.CoreRolePermissionService;
import vn.tr.core.data.*;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CoreMenuBusiness {
	
	private final CoreMenuService coreMenuService;
	private final CoreRolePermissionService coreRole2MenuService;
	
	private CoreMenuData convertToCoreMenuData(CoreMenu coreMenu) {
		CoreMenuData coreMenuData = new CoreMenuData();
		coreMenuData.setId(coreMenu.getId());
		coreMenuData.setTen(coreMenu.getTen());
		coreMenuData.setMa(coreMenu.getMa());
		coreMenuData.setChaId(null);
		if (Objects.nonNull(coreMenu.getChaId())) {
			Optional<CoreMenu> optionalCoreMenu = coreMenuService.findByIdAndDaXoaFalse(coreMenu.getChaId());
			if (optionalCoreMenu.isPresent()) {
				coreMenuData.setChaId(optionalCoreMenu.get().getId());
				coreMenuData.setChaTen(optionalCoreMenu.get().getTen());
			}
		}
		coreMenuData.setMoTa(coreMenu.getMoTa());
		coreMenuData.setPath(coreMenu.getPath());
		coreMenuData.setComponent(coreMenu.getComponent());
		coreMenuData.setIsHidden(coreMenu.getIsHidden());
		coreMenuData.setIcon(coreMenu.getIcon());
		coreMenuData.setIsAlwaysShow(coreMenu.getIsAlwaysShow());
		coreMenuData.setIsNoCache(coreMenu.getIsNoCache());
		coreMenuData.setIsAffix(coreMenu.getIsAffix());
		coreMenuData.setIsBreadcrumb(coreMenu.getIsBreadcrumb());
		coreMenuData.setLink(coreMenu.getLink());
		coreMenuData.setActiveMenu(coreMenu.getActiveMenu());
		coreMenuData.setProps(coreMenu.getProps());
		coreMenuData.setIsReload(coreMenu.getIsReload());
		coreMenuData.setTrangThai(coreMenu.getTrangThai());
		coreMenuData.setSapXep(coreMenu.getSapXep());
		return coreMenuData;
	}
	
	public CoreMenuData create(CoreMenuData coreMenuData) {
		CoreMenu coreMenu = new CoreMenu();
		return save(coreMenu, coreMenuData);
	}
	
	public void delete(@PathVariable("id") Long id) throws EntityNotFoundException {
		Optional<CoreMenu> optional = coreMenuService.findById(id);
		if (optional.isEmpty()) {
			throw new EntityNotFoundException(CoreMenu.class, id);
		}
		CoreMenu coreMenu = optional.get();
		coreMenu.setDaXoa(true);
		coreMenuService.save(coreMenu);
	}
	
	public void deleteByIds(List<Long> ids) {
		if (CollUtil.isNotEmpty(ids)) {
			coreMenuService.setFixedDaXoaForIds(true, ids);
		}
	}
	
	public Page<CoreMenuData> findAll(int page, int size, String sortBy, String sortDir, String search, Boolean trangThai, String appCode) {
		Pageable pageable = CoreUtils.getPageRequest(page, size, sortBy, sortDir);
		Page<CoreMenu> pageCoreMenu = coreMenuService.findAll(search, trangThai, appCode, pageable);
		return pageCoreMenu.map(this::convertToCoreMenuData);
	}
	
	public CoreMenuData findById(Long id) throws EntityNotFoundException {
		Optional<CoreMenu> optional = coreMenuService.findById(id);
		if (optional.isEmpty()) {
			throw new EntityNotFoundException(CoreMenu.class, id);
		}
		CoreMenu coreMenu = optional.get();
		return convertToCoreMenuData(coreMenu);
	}
	
	public List<CoreMenuData> getAll(List<Long> ids) {
		if (CollUtil.isNotEmpty(ids)) {
			return coreMenuService.findByIdInAndDaXoaFalse(ids).stream().map(this::convertToCoreMenuData).toList();
		}
		return coreMenuService.findByDaXoaFalse().stream().map(this::convertToCoreMenuData).toList();
	}
	
	public CoreDsMenuData getRouterDatas(String appCode) {
		String email = LoginHelper.getUsername();
		Set<String> roles = new HashSet<>();
		LoginUser loginUser = LoginHelper.getLoginUser();
		if (Objects.nonNull(loginUser)) {
			roles = loginUser.getRoleCodes();
		}
		
		CoreDsMenuData coreDsMenuData = new CoreDsMenuData();
		coreDsMenuData.setEmail(email);
		coreDsMenuData.setRoles(roles);
		List<CoreMenu> coreMenus;
		//boolean isRoot = LoginHelper.isSuperAdmin();
		//log.info("isRoot: {} - roles: {}", isRoot, roles);
		if (CollUtil.contains(roles, "ROLE_ADMIN")) {
			coreMenus = coreMenuService.findByTrangThaiTrueAndAppCodeAndDaXoaFalse(appCode);
		} else {
			List<Long> menuIds = coreRole2MenuService.getMenuIds(roles);
			log.info("appCode: {} - menuIds: {}", appCode, menuIds);
			coreMenus = coreMenuService.findByIdInAndTrangThaiTrueAndAppCodeAndDaXoaFalse(menuIds, appCode);
		}
		if (CollUtil.isNotEmpty(coreMenus)) {
			List<CoreMenu> cMenus = coreMenus.stream()
					.filter(e -> Objects.isNull(e.getChaId()))
					.sorted(Comparator.comparingInt(CoreMenu::getSapXep))
					.toList();
			coreDsMenuData.setCoreMenuDatas(setCoreMenuData(cMenus, coreMenus));
		}
		
		return coreDsMenuData;
	}
	
	private CoreMenuData save(CoreMenu coreMenu, CoreMenuData coreMenuData) {
		coreMenu.setDaXoa(false);
		coreMenu.setTen(FunctionUtils.removeXss(coreMenuData.getTen()));
		coreMenu.setMa(FunctionUtils.removeXss(coreMenuData.getMa()));
		coreMenu.setChaId(null);
		if (Objects.nonNull(coreMenuData.getChaId())) {
			Optional<CoreMenu> optionalCoreMenu = coreMenuService.findByIdAndDaXoaFalse(coreMenuData.getChaId());
			if (optionalCoreMenu.isPresent()) {
				coreMenu.setChaId(optionalCoreMenu.get().getId());
			}
		}
		coreMenu.setMoTa(FunctionUtils.removeXss(coreMenuData.getMoTa()));
		coreMenu.setPath(FunctionUtils.removeXss(coreMenuData.getPath()));
		coreMenu.setComponent(FunctionUtils.removeXss(coreMenuData.getComponent()));
		coreMenu.setRedirect(FunctionUtils.removeXss(coreMenuData.getRedirect()));
		coreMenu.setIsHidden(Boolean.TRUE.equals(coreMenuData.getIsHidden()));
		coreMenu.setIcon(FunctionUtils.removeXss(coreMenuData.getIcon()));
		coreMenu.setIsAlwaysShow(Boolean.TRUE.equals(coreMenuData.getIsAlwaysShow()));
		coreMenu.setIsNoCache(Boolean.TRUE.equals(coreMenuData.getIsNoCache()));
		coreMenu.setIsAffix(Boolean.TRUE.equals(coreMenuData.getIsAffix()));
		coreMenu.setIsBreadcrumb(Boolean.TRUE.equals(coreMenuData.getIsBreadcrumb()));
		coreMenu.setLink(FunctionUtils.removeXss(coreMenuData.getLink()));
		coreMenu.setActiveMenu(FunctionUtils.removeXss(coreMenuData.getActiveMenu()));
		coreMenu.setProps(FunctionUtils.removeXss(coreMenuData.getProps()));
		coreMenu.setIsReload(Boolean.TRUE.equals(coreMenuData.getIsReload()));
		coreMenu.setTrangThai(Boolean.TRUE.equals(coreMenuData.getTrangThai()));
		coreMenu.setSapXep(coreMenuData.getSapXep());
		coreMenu = coreMenuService.save(coreMenu);
		return convertToCoreMenuData(coreMenu);
	}
	
	public void saveRouterData(RouterData routerData, Long chaId, int sapXep, String appCode) {
		log.info("Router: {}", routerData.getName());
		Optional<CoreMenu> optionalCoreMenu = coreMenuService.findFirstByMaIgnoreCaseAndAppCodeIgnoreCase(routerData.getName(), appCode);
		CoreMenu coreMenu = new CoreMenu();
		if (optionalCoreMenu.isPresent()) {
			coreMenu = optionalCoreMenu.get();
		}
		coreMenu.setDaXoa(false);
		coreMenu.setIsReload(true);
		coreMenu.setTrangThai(true);
		coreMenu.setAppCode(FunctionUtils.removeXss(appCode));
		coreMenu.setChaId(chaId);
		coreMenu.setMa(FunctionUtils.removeXss(routerData.getName()));
		coreMenu.setPath(FunctionUtils.removeXss(routerData.getPath()));
		coreMenu.setRedirect(FunctionUtils.removeXss(routerData.getRedirect()));
		coreMenu.setIsHidden(Boolean.TRUE.equals(routerData.getHidden()));
		coreMenu.setIsAlwaysShow(Boolean.TRUE.equals(routerData.getAlwaysShow()));
		coreMenu.setSapXep(sapXep);
		coreMenu.setProps(JsonUtils.toJsonString(routerData.getProps()));
		
		if (Objects.nonNull(routerData.getMeta())) {
			MetaData metaData = routerData.getMeta();
			coreMenu.setIcon(FunctionUtils.removeXss(metaData.getIcon()));
			coreMenu.setTen(FunctionUtils.removeXss(metaData.getTitle()));
			coreMenu.setMoTa(FunctionUtils.removeXss(metaData.getTitle()));
			coreMenu.setActiveMenu(FunctionUtils.removeXss(metaData.getActiveMenu()));
			coreMenu.setIsAffix(Boolean.TRUE.equals(metaData.getAffix()));
			coreMenu.setIsBreadcrumb(Boolean.TRUE.equals(metaData.getBreadcrumb()));
			coreMenu.setIsNoCache(Boolean.TRUE.equals(metaData.getNoCache()));
			coreMenu.setComponent(FunctionUtils.removeXss(metaData.getComponent()));
			coreMenu.setLink(FunctionUtils.removeXss(metaData.getLink()));
		}
		coreMenu = coreMenuService.save(coreMenu);
		
		int sapXepCon = 0;
		if (CollUtil.isNotEmpty(routerData.getChildren())) {
			for (RouterData children : routerData.getChildren()) {
				sapXepCon++;
				saveRouterData(children, coreMenu.getId(), sapXepCon, appCode);
			}
		}
	}
	
	private List<CoreMenuData> setCoreMenuData(List<CoreMenu> cMenus, List<CoreMenu> coreMenus) {
		List<CoreMenuData> coreMenuDatas = new ArrayList<>();
		if (CollUtil.isNotEmpty(cMenus)) {
			for (CoreMenu coreMenu : cMenus) {
				CoreMenuData coreMenuData = new CoreMenuData();
				coreMenuData.setId(coreMenu.getId());
				coreMenuData.setTen(coreMenu.getTen());
				coreMenuData.setMa(coreMenu.getMa());
				coreMenuData.setChaId(coreMenu.getChaId());
				coreMenuData.setMoTa(coreMenu.getMoTa());
				coreMenuData.setPath(coreMenu.getPath());
				coreMenuData.setComponent(coreMenu.getComponent());
				coreMenuData.setRedirect(coreMenu.getRedirect());
				coreMenuData.setIsHidden(coreMenu.getIsHidden());
				coreMenuData.setIcon(coreMenu.getIcon());
				coreMenuData.setIsAlwaysShow(coreMenu.getIsAlwaysShow());
				coreMenuData.setIsNoCache(coreMenu.getIsNoCache());
				coreMenuData.setIsAffix(coreMenu.getIsAffixTab());
				coreMenuData.setIsBreadcrumb(coreMenu.getIsBreadcrumb());
				coreMenuData.setLink(coreMenu.getLink());
				coreMenuData.setActiveMenu(coreMenu.getActiveMenu());
				coreMenuData.setProps(coreMenu.getProps());
				coreMenuData.setIsReload(coreMenu.getIsReload());
				coreMenuData.setTrangThai(coreMenu.getTrangThai());
				coreMenuData.setSapXep(coreMenu.getSapXep());
				coreMenuData.setAppCode(coreMenu.getAppCode());
				
				List<CoreMenu> children = coreMenus.stream()
						.filter(e -> Objects.nonNull(e.getChaId()))
						.filter(e -> e.getChaId().equals(coreMenu.getId()))
						.sorted(Comparator.comparingInt(CoreMenu::getSapXep))
						.toList();
				coreMenuData.setChildren(setCoreMenuData(children, coreMenus));
				coreMenuDatas.add(coreMenuData);
			}
		}
		return coreMenuDatas;
	}
	
	public void setRouterDatas(Object object, String appCode) {
		log.info("Bắt đầu get dữ liệu router");
		long start = System.currentTimeMillis();
		if (Objects.nonNull(object)) {
			ObjectMapper mapper = new ObjectMapper();
			List<RouterData> routerDatas = mapper.convertValue(object, new TypeReference<>() {
			});
			coreMenuService.setFixedDaXoaAndAppCode(true, appCode);
			if (CollUtil.isNotEmpty(routerDatas)) {
				int sapXep = 0;
				for (RouterData routerData : routerDatas) {
					sapXep++;
					saveRouterData(routerData, null, sapXep, appCode);
				}
				
			}
			log.info("Đã hoàn thành get dữ liệu router, tổng thời gian save menu, {}", System.currentTimeMillis() - start);
		}
	}
	
	public CoreMenuData update(Long id, CoreMenuData coreMenuData) throws EntityNotFoundException {
		Optional<CoreMenu> optionalCoreMenu = coreMenuService.findById(id);
		if (optionalCoreMenu.isEmpty()) {
			throw new EntityNotFoundException(CoreMenu.class, id);
		}
		CoreMenu coreMenu = optionalCoreMenu.get();
		return save(coreMenu, coreMenuData);
	}
	
	public List<RouteRecordRawData> getRoutes(String appCode) {
		LoginUser loginUser = LoginHelper.getLoginUser();
		List<RouteRecordRawData> routeRecordRawDatas = new ArrayList<>();
		if (Objects.nonNull(loginUser) && loginUser.getUserType().equals("sys_user")) {
			Set<String> roles = loginUser.getRoleCodes();
			
			List<CoreMenu> coreMenus;
			//boolean isRoot = LoginHelper.isSuperAdmin();
			//log.info("isRoot: {} - roles: {}", isRoot, roles);
			if (CollUtil.contains(roles, "ROLE_ADMIN")) {
				coreMenus = coreMenuService.findByTrangThaiTrueAndAppCodeAndDaXoaFalse(appCode);
			} else {
				List<Long> menuIds = coreRole2MenuService.getMenuIds(roles);
				log.info("getRoutes appCode: {} - menuIds: {}", appCode, menuIds);
				coreMenus = coreMenuService.findByIdInAndTrangThaiTrueAndAppCodeAndDaXoaFalse(menuIds, appCode);
			}
			if (CollUtil.isNotEmpty(coreMenus)) {
				List<CoreMenu> cMenus = coreMenus.stream()
						.filter(e -> Objects.isNull(e.getChaId()))
						.sorted(Comparator.comparingInt(CoreMenu::getSapXep))
						.toList();
				routeRecordRawDatas = setRouteRecordRawData(cMenus, coreMenus);
			}
		}
		return routeRecordRawDatas;
		
	}
	
	private List<RouteRecordRawData> setRouteRecordRawData(List<CoreMenu> cMenus, List<CoreMenu> coreMenus) {
		List<RouteRecordRawData> routeRecordRawDatas = new ArrayList<>();
		if (CollUtil.isNotEmpty(cMenus)) {
			for (CoreMenu coreMenu : cMenus) {
				RouteRecordRawData routeRecordRawData = new RouteRecordRawData();
				routeRecordRawData.setName(coreMenu.getMa());
				routeRecordRawData.setPath(coreMenu.getPath());
				routeRecordRawData.setComponent(coreMenu.getComponent());
				routeRecordRawData.setRedirect(coreMenu.getRedirect());
				routeRecordRawData.setProps(coreMenu.getProps());
				
				RouterMetaData routerMetaData = new RouterMetaData();
				routerMetaData.setTitle(coreMenu.getTen());
				routerMetaData.setLayout(coreMenu.getLayout());
				routerMetaData.setNoBasicLayout(Boolean.TRUE.equals(coreMenu.getIsNoBasicLayout()));
				routerMetaData.setIcon(coreMenu.getIcon());
				routerMetaData.setLoaded(Boolean.TRUE.equals(coreMenu.getIsLoaded()));
				routerMetaData.setKeepAlive(Boolean.TRUE.equals(coreMenu.getIsKeepAlive()));
				routerMetaData.setAffixTab(coreMenu.getIsAffixTab());
				routerMetaData.setAffixTabOrder(coreMenu.getAffixTabOrder());
				routerMetaData.setQuery(coreMenu.getQuery());
				
				routerMetaData.setOpenInNewWindow(Boolean.TRUE.equals(coreMenu.getIsOpenInNewWindow()));
				routerMetaData.setMenuVisibleWithForbidden(Boolean.TRUE.equals(coreMenu.getIsMenuVisibleWithForbidden()));
				routerMetaData.setNoCache(Boolean.TRUE.equals(coreMenu.getIsNoCache()));
				routerMetaData.setKeepAlive(Boolean.TRUE.equals(coreMenu.getIsKeepAlive()));
				
				routerMetaData.setHideInMenu(Boolean.TRUE.equals(coreMenu.getIsHideInMenu()));
				routerMetaData.setHideInBreadcrumb(Boolean.TRUE.equals(coreMenu.getIsHideInBreadcrumb()));
				routerMetaData.setHideChildrenInMenu(Boolean.TRUE.equals(coreMenu.getIsHideChildrenInMenu()));
				routerMetaData.setHideInTab(Boolean.TRUE.equals(coreMenu.getIsHideInTab()));
				
				routerMetaData.setActiveMenu(coreMenu.getActiveMenu());
				routerMetaData.setActiveIcon(coreMenu.getActiveIcon());
				routerMetaData.setActivePath(coreMenu.getActivePath());
				
				routerMetaData.setLink(coreMenu.getLink());
				routerMetaData.setIframeSrc(coreMenu.getIframeSrc());
				routerMetaData.setOrder(coreMenu.getSapXep());
				routerMetaData.setMaxNumOfOpenTab(coreMenu.getMaxNumOfOpenTab());
				routerMetaData.setMenuVisibleWithForbidden(Boolean.TRUE.equals(coreMenu.getIsMenuVisibleWithForbidden()));
				routerMetaData.setIgnoreAccess(Boolean.TRUE.equals(coreMenu.getIsIgnoreAccess()));
				
				routerMetaData.setBadge(coreMenu.getBadge());
				routerMetaData.setBadgeType(coreMenu.getBadgeType());
				routerMetaData.setBadgeVariants(coreMenu.getBadgeVariants());
				
				routeRecordRawData.setMeta(routerMetaData);
				List<CoreMenu> children = coreMenus.stream()
						.filter(e -> Objects.nonNull(e.getChaId()))
						.filter(e -> e.getChaId().equals(coreMenu.getId()))
						.sorted(Comparator.comparingInt(CoreMenu::getSapXep))
						.toList();
				routeRecordRawData.setChildren(setRouteRecordRawData(children, coreMenus));
				routeRecordRawDatas.add(routeRecordRawData);
			}
		}
		return routeRecordRawDatas;
	}
	
	public void setRoutes(Object object, String appCode) {
		log.info("Bắt đầu get dữ liệu router");
		long start = System.currentTimeMillis();
		if (Objects.nonNull(object)) {
			ObjectMapper mapper = new ObjectMapper();
			List<RouteRecordRawData> routeRecordRawDatas = mapper.convertValue(object, new TypeReference<>() {
			});
			coreMenuService.setFixedDaXoaAndAppCode(true, appCode);
			if (CollUtil.isNotEmpty(routeRecordRawDatas)) {
				int sapXep = 0;
				for (RouteRecordRawData routeRecordRawData : routeRecordRawDatas) {
					sapXep++;
					saveRouteRecordRawData(routeRecordRawData, null, sapXep, appCode);
				}
				
			}
			log.info("Đã hoàn thành get dữ liệu router, tổng thời gian save menu, {}", System.currentTimeMillis() - start);
		}
	}
	
	public void saveRouteRecordRawData(RouteRecordRawData routeRecordRawData, Long chaId, int sapXep, String appCode) {
		log.info("RouteRecordRaw: {}", routeRecordRawData.getName());
		Optional<CoreMenu> optionalCoreMenu = coreMenuService.findFirstByMaIgnoreCaseAndAppCodeIgnoreCase(routeRecordRawData.getName(), appCode);
		CoreMenu coreMenu = new CoreMenu();
		if (optionalCoreMenu.isPresent()) {
			coreMenu = optionalCoreMenu.get();
		}
		coreMenu.setDaXoa(false);
		coreMenu.setIsReload(true);
		coreMenu.setTrangThai(true);
		coreMenu.setAppCode(FunctionUtils.removeXss(appCode));
		coreMenu.setChaId(chaId);
		coreMenu.setMa(FunctionUtils.removeXss(routeRecordRawData.getName()));
		coreMenu.setPath(FunctionUtils.removeXss(routeRecordRawData.getPath()));
		coreMenu.setRedirect(FunctionUtils.removeXss(routeRecordRawData.getRedirect()));
		coreMenu.setComponent(FunctionUtils.removeXss(routeRecordRawData.getComponent()));
		coreMenu.setSapXep(sapXep);
		coreMenu.setProps(JsonUtils.toJsonString(routeRecordRawData.getProps()));
		
		if (Objects.nonNull(routeRecordRawData.getMeta())) {
			RouterMetaData routerMetaData = routeRecordRawData.getMeta();
			coreMenu.setIcon(FunctionUtils.removeXss(routerMetaData.getIcon()));
			coreMenu.setTen(FunctionUtils.removeXss(routerMetaData.getTitle()));
			coreMenu.setMoTa(FunctionUtils.removeXss(routerMetaData.getTitle()));
			coreMenu.setActiveMenu(FunctionUtils.removeXss(routerMetaData.getActiveMenu()));
			coreMenu.setActiveIcon(FunctionUtils.removeXss(routerMetaData.getActiveIcon()));
			coreMenu.setActivePath(FunctionUtils.removeXss(routerMetaData.getActivePath()));
			coreMenu.setIsAffixTab(Boolean.TRUE.equals(routerMetaData.getAffixTab()));
			coreMenu.setAffixTabOrder(routerMetaData.getAffixTabOrder());
			coreMenu.setBadge(routerMetaData.getBadge());
			coreMenu.setBadgeType(routerMetaData.getBadgeType());
			coreMenu.setBadgeVariants(routerMetaData.getBadgeVariants());
			coreMenu.setIsHideChildrenInMenu(Boolean.TRUE.equals(routerMetaData.getHideChildrenInMenu()));
			coreMenu.setIsHideInTab(Boolean.TRUE.equals(routerMetaData.getHideInTab()));
			coreMenu.setIsHideInBreadcrumb(Boolean.TRUE.equals(routerMetaData.getHideInBreadcrumb()));
			coreMenu.setIsHideInMenu(Boolean.TRUE.equals(routerMetaData.getHideInMenu()));
			coreMenu.setIframeSrc(routerMetaData.getIframeSrc());
			coreMenu.setIsIgnoreAccess(Boolean.TRUE.equals(routerMetaData.getIgnoreAccess()));
			coreMenu.setIsKeepAlive(Boolean.TRUE.equals(routerMetaData.getKeepAlive()));
			coreMenu.setLayout(routerMetaData.getLayout());
			coreMenu.setLink(FunctionUtils.removeXss(routerMetaData.getLink()));
			coreMenu.setIsLoaded(Boolean.TRUE.equals(routerMetaData.getLoaded()));
			coreMenu.setMaxNumOfOpenTab(routerMetaData.getMaxNumOfOpenTab());
			coreMenu.setIsMenuVisibleWithForbidden(Boolean.TRUE.equals(routerMetaData.getMenuVisibleWithForbidden()));
			coreMenu.setIsNoBasicLayout(Boolean.TRUE.equals(routerMetaData.getNoBasicLayout()));
			coreMenu.setIsNoCache(Boolean.TRUE.equals(routerMetaData.getNoCache()));
			coreMenu.setIsOpenInNewWindow(Boolean.TRUE.equals(routerMetaData.getOpenInNewWindow()));
			coreMenu.setQuery(routerMetaData.getQuery());
		}
		coreMenu = coreMenuService.save(coreMenu);
		
		int sapXepCon = 0;
		if (CollUtil.isNotEmpty(routeRecordRawData.getChildren())) {
			for (RouteRecordRawData children : routeRecordRawData.getChildren()) {
				sapXepCon++;
				saveRouteRecordRawData(children, coreMenu.getId(), sapXepCon, appCode);
			}
		}
	}
	
}
