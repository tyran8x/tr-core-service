package vn.tr.core.dao.service;

import vn.tr.core.dao.model.CoreUserRole;

import java.util.Optional;
import java.util.Set;

public interface CoreUserRoleService {
	
	void deleteById(Long id);
	
	boolean existsById(Long id);
	
	Optional<CoreUserRole> findById(Long id);
	
	CoreUserRole save(CoreUserRole coreUserRole);
	
	void replaceUserRolesForApp(String username, String appCode, Set<String> newRoleCodesInApp);
	
	Set<String> findRoleCodesByUsername(String username);
	
	void assignRoleToUserIfNotExists(String username, String appCode, String roleCode);
	
}
