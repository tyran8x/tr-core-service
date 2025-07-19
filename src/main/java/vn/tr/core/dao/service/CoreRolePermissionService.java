package vn.tr.core.dao.service;

import vn.tr.core.dao.model.CoreRolePermission;

import java.util.Optional;

public interface CoreRolePermissionService {
	
	void deleteById(Long id);
	
	boolean existsById(Long id);
	
	Optional<CoreRolePermission> findById(Long id);
	
	CoreRolePermission save(CoreRolePermission coreRolePermission);
	
	void refreshRolePermsCache();
	
	void refreshRolePermsCache(String roleMa);
	
	void refreshRolePermsCache(String oldRoleMa, String newRoleMa);
	
}
