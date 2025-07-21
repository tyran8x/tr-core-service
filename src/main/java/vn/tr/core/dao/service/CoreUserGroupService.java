package vn.tr.core.dao.service;

import vn.tr.core.dao.model.CoreUserGroup;

import java.util.Optional;
import java.util.Set;

public interface CoreUserGroupService {
	
	CoreUserGroup save(CoreUserGroup coreUser2Group);
	
	void deleteById(Long id);
	
	boolean existsById(Long id);
	
	Optional<CoreUserGroup> findById(Long id);
	
	void synchronizeUserGroupsInApp(String username, String appCode, Set<String> newGroupCodes);
	
	void assignUserToGroupInApp(String username, String appCode, String groupCode);
	
	Set<String> findActiveGroupCodesByUsernameAndAppCode(String username, String appCode);
	
	Set<String> findAllActiveGroupCodesByUsername(String username);
	
}
