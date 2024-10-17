package vn.tr.core.dao.service;

import vn.tr.core.dao.model.CoreUser2Role;

import java.util.List;
import java.util.Optional;

public interface CoreUser2RoleService {

	CoreUser2Role save(CoreUser2Role coreUser2Role);

	void deleteById(Long id);

	boolean existsById(Long id);

	Optional<CoreUser2Role> findById(Long id);

	List<CoreUser2Role> findByRoleAndDaXoaFalse(String role);

	Optional<CoreUser2Role> findFirstByRoleAndUserName(String role, String userName);

	void setFixedDaXoaForRole(boolean daXoa, String role);

	void setFixedDaXoaForUserName(boolean daXoa, String userName);

	List<CoreUser2Role> findByUserNameAndDaXoaFalse(String userName);

	List<String> getRoleByUserName(String userName);
	
}
