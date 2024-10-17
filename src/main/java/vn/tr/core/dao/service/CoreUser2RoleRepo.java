package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreUser2Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoreUser2RoleRepo extends JpaRepository<CoreUser2Role, Long>, JpaSpecificationExecutor<CoreUser2Role> {

	List<CoreUser2Role> findByRoleAndDaXoaFalse(String role);

	Optional<CoreUser2Role> findFirstByRoleAndUserName(String role, String userName);

	@Modifying(clearAutomatically = true)
	@Query("update CoreUser2Role u set u.daXoa = ?1 where u.role = ?2")
	void setFixedDaXoaForRole(boolean daXoa, String role);

	@Modifying(clearAutomatically = true)
	@Query("update CoreUser2Role u set u.daXoa = ?1 where u.userName = ?2")
	void setFixedDaXoaForUserName(boolean daXoa, String userName);

	List<CoreUser2Role> findByUserNameAndDaXoaFalse(String userName);

	@Query("SELECT distinct u.role FROM CoreUser2Role u WHERE u.daXoa = false AND u.userName = ?1")
	List<String> getRoleByUserName(String userName);
}
