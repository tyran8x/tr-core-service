package vn.tr.core.dao.service;

import vn.tr.core.dao.model.CoreUserRole;

import java.util.List;
import java.util.Optional;

public interface CoreUserRoleService {
	
	CoreUserRole save(CoreUserRole coreUserRole);
	
	void deleteById(Long id);
	
	boolean existsById(Long id);
	
	Optional<CoreUserRole> findById(Long id);
	
	List<CoreUserRole> findByRoleAndDaXoaFalse(String role);
	
	Optional<CoreUserRole> findFirstByRoleAndUserName(String role, String userName);
	
	void setFixedDaXoaForRole(boolean daXoa, String role);
	
	void setFixedDaXoaForUserName(boolean daXoa, String userName);
	
	List<CoreUserRole> findByUserNameAndDaXoaFalse(String userName);
	
	List<String> getRoleByUserName(String userName);
	
}
