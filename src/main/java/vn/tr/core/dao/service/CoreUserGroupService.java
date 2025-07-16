package vn.tr.core.dao.service;

import vn.tr.core.dao.model.CoreUserGroup;

import java.util.List;
import java.util.Optional;

public interface CoreUserGroupService {
	
	CoreUserGroup save(CoreUserGroup coreUser2Group);
	
	void deleteById(Long id);
	
	boolean existsById(Long id);
	
	Optional<CoreUserGroup> findById(Long id);
	
	List<CoreUserGroup> findByGroupIdAndDaXoaFalse(Long groupId);
	
	Optional<CoreUserGroup> findFirstByGroupIdAndUserName(Long groupId, String userName);
	
	void setFixedDaXoaForGroupId(boolean daXoa, Long groupId);
	
	void setFixedDaXoaForUserName(boolean daXoa, String userName);
	
	List<CoreUserGroup> findByUserNameAndDaXoaFalse(String userName);
	
}
