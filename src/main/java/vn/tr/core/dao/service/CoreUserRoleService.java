package vn.tr.core.dao.service;

import vn.tr.core.dao.model.CoreUserRole;

import java.util.Optional;
import java.util.Set;

public interface CoreUserRoleService {
	
	void deleteById(Long id);
	
	boolean existsById(Long id);
	
	Optional<CoreUserRole> findById(Long id);
	
	CoreUserRole save(CoreUserRole coreUserRole);
	
	void synchronizeUserRolesInApp(String username, String appCode, Set<String> newRoleCodes);
	
	void assignRoleToUserInApp(String username, String appCode, String roleCode);
	
	Set<String> findActiveRoleCodesByUsernameAndAppCode(String username, String appCode);
	
	Set<String> findAllActiveRoleCodesByUsername(String username);
	
}
