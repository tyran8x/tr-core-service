package vn.tr.core.dao.service;

import vn.tr.core.dao.model.CoreRolePermission;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CoreRolePermissionService {
	
	void deleteById(Long id);
	
	boolean existsById(Long id);
	
	List<CoreRolePermission> findByDaXoaFalse();
	
	Optional<CoreRolePermission> findById(Long id);
	
	List<CoreRolePermission> findByMenuIdAndDaXoaFalse(Long menuId);
	
	List<CoreRolePermission> findByRoleIdAndDaXoaFalse(Long roleId);
	
	Optional<CoreRolePermission> findFirstByRoleIdAndMenuId(Long roleId, Long menuId);
	
	Set<String> getMenuMas(Set<String> roles);
	
	CoreRolePermission save(CoreRolePermission coreRolePermission);
	
	void setFixedDaXoaForRoleCode(boolean daXoa, String roleCode);
	
	void refreshRolePermsCache();
	
	void refreshRolePermsCache(String roleMa);
	
	void refreshRolePermsCache(String oldRoleMa, String newRoleMa);
	
}
