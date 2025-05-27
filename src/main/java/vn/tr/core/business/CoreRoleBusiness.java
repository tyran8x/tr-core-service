package vn.tr.core.business;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.core.utils.FunctionUtils;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.core.dao.model.CoreMenu;
import vn.tr.core.dao.model.CoreRole;
import vn.tr.core.dao.model.CoreRole2Menu;
import vn.tr.core.dao.service.CoreMenuService;
import vn.tr.core.dao.service.CoreRole2MenuService;
import vn.tr.core.dao.service.CoreRoleService;
import vn.tr.core.data.CoreRoleData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoreRoleBusiness {
	
	private final CoreRoleService coreRoleService;
	private final CoreRole2MenuService coreRole2MenuService;
	private final CoreMenuService coreMenuService;
	
	private CoreRoleData convertToCoreRoleData(CoreRole coreRole) {
		CoreRoleData coreRoleData = new CoreRoleData();
		coreRoleData.setId(coreRole.getId());
		coreRoleData.setTen(coreRole.getTen());
		coreRoleData.setMa(coreRole.getMa());
		coreRoleData.setIsDefault(Boolean.TRUE.equals(coreRole.getIsDefault()));
		coreRoleData.setMoTa(coreRole.getMoTa());
		coreRoleData.setTrangThai(Boolean.TRUE.equals(coreRole.getTrangThai()));
		coreRoleData.setAppCode(coreRole.getAppCode());
		List<CoreRole2Menu> coreRole2Menus = coreRole2MenuService.findByRoleIdAndDaXoaFalse(coreRole.getId());
		List<CoreMenu> coreMenus = coreMenuService.findByIdInAndDaXoaFalse(coreRole2Menus.stream().map(
				CoreRole2Menu::getMenuId).collect(Collectors.toList()));
		List<String> menus = new ArrayList<>();
		if (CollUtil.isNotEmpty(coreMenus)) {
			for (CoreMenu coreMenu : coreMenus) {
				menus.add(coreMenu.getMa());
			}
		}
		coreRoleData.setMenus(menus);
		return coreRoleData;
	}
	
	public CoreRoleData create(CoreRoleData coreRoleData) {
		CoreRole coreRole = new CoreRole();
		return save(coreRole, coreRoleData);
	}
	
	public void delete(Long id) throws EntityNotFoundException {
		Optional<CoreRole> optional = coreRoleService.findById(id);
		if (optional.isEmpty()) {
			throw new EntityNotFoundException(CoreRole.class, id);
		}
		CoreRole coreRole = optional.get();
		coreRole.setDaXoa(true);
		coreRoleService.save(coreRole);
	}
	
	public Page<CoreRoleData> findAll(int page, int size, String sortBy, String sortDir, String search, Boolean trangThai, String appCode) {
		Pageable pageable = CoreUtils.getPageRequest(page, size, sortBy, sortDir);
		Page<CoreRole> pageCoreRole = coreRoleService.findAll(search, trangThai, appCode, pageable);
		return pageCoreRole.map(this::convertToCoreRoleData);
	}
	
	public CoreRoleData findById(Long id) throws EntityNotFoundException {
		Optional<CoreRole> optional = coreRoleService.findById(id);
		if (optional.isEmpty()) {
			throw new EntityNotFoundException(CoreRole.class, id);
		}
		return convertToCoreRoleData(optional.get());
	}
	
	private CoreRoleData save(CoreRole coreRole, CoreRoleData coreRoleData) {
		coreRole.setDaXoa(false);
		coreRole.setTen(FunctionUtils.removeXss(coreRoleData.getTen()));
		coreRole.setMa(FunctionUtils.removeXss(coreRoleData.getMa()));
		coreRole.setIsDefault(Boolean.TRUE.equals(coreRoleData.getIsDefault()));
		coreRole.setMoTa(FunctionUtils.removeXss(coreRoleData.getMoTa()));
		coreRole.setTrangThai(Boolean.TRUE.equals(coreRoleData.getTrangThai()));
		coreRole.setAppCode(FunctionUtils.removeXss(coreRoleData.getAppCode()));
		coreRole = coreRoleService.save(coreRole);
		coreRole2MenuService.setFixedDaXoaForRoleId(true, coreRole.getId());
		if (CollUtil.isNotEmpty(coreRoleData.getMenus())) {
			for (String menu : coreRoleData.getMenus()) {
				if (CharSequenceUtil.isNotBlank(menu)) {
					Optional<CoreMenu> optionalCoreMenu = coreMenuService.findFirstByMaIgnoreCaseAndAppCodeIgnoreCase(menu,
							coreRoleData.getAppCode());
					if (optionalCoreMenu.isPresent()) {
						CoreRole2Menu coreRole2Menu = new CoreRole2Menu();
						Optional<CoreRole2Menu> optionalCoreRole2Menu = coreRole2MenuService.findFirstByRoleIdAndMenuId(coreRole.getId(),
								optionalCoreMenu.get().getId());
						if (optionalCoreRole2Menu.isPresent()) {
							coreRole2Menu = optionalCoreRole2Menu.get();
						}
						coreRole2Menu.setDaXoa(false);
						coreRole2Menu.setMenuId(optionalCoreMenu.get().getId());
						coreRole2Menu.setRoleId(coreRole.getId());
						coreRole2MenuService.save(coreRole2Menu);
					}
				}
			}
		}
		return convertToCoreRoleData(coreRole);
	}
	
	public CoreRoleData update(Long id, CoreRoleData coreRoleData) throws EntityNotFoundException {
		Optional<CoreRole> optional = coreRoleService.findById(id);
		if (optional.isEmpty()) {
			throw new EntityNotFoundException(CoreRole.class, id);
		}
		CoreRole coreRole = optional.get();
		return save(coreRole, coreRoleData);
	}
	
}
