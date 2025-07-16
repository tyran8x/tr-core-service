package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreUserRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoreUserRoleRepo extends JpaRepository<CoreUserRole, Long>, JpaSpecificationExecutor<CoreUserRole> {
	
	List<CoreUserRole> findByRoleAndDaXoaFalse(String role);
	
	Optional<CoreUserRole> findFirstByRoleAndUserName(String role, String userName);
	
	@Modifying(clearAutomatically = true)
	@Query("update CoreUserRole u set u.daXoa = ?1 where u.role = ?2")
	void setFixedDaXoaForRole(boolean daXoa, String role);
	
	@Modifying(clearAutomatically = true)
	@Query("update CoreUserRole u set u.daXoa = ?1 where u.userName = ?2")
	void setFixedDaXoaForUserName(boolean daXoa, String userName);
	
	List<CoreUserRole> findByUserNameAndDaXoaFalse(String userName);
	
	@Query("SELECT distinct u.roleId FROM CoreUserRole u WHERE u.daXoa = false AND u.userName = ?1")
	List<String> getRoleByUserName(String userName);
}
