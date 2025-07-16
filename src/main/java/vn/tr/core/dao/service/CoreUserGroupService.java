package vn.tr.core.dao.service;

import vn.tr.core.dao.model.CoreUserGroup;

import java.util.Optional;
import java.util.Set;

public interface CoreUserGroupService {
	
	CoreUserGroup save(CoreUserGroup coreUser2Group);
	
	void deleteById(Long id);
	
	boolean existsById(Long id);
	
	Optional<CoreUserGroup> findById(Long id);
	
	void replaceUserGroups(String username, Set<String> newGroupCodes);
	
}
