package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CorePermission;

import java.util.List;
import java.util.Set;

@Repository
public interface CorePermissionRepo extends JpaRepository<CorePermission, Long>, JpaSpecificationExecutor<CorePermission> {
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CorePermission g SET g.deletedAt = CURRENT_TIMESTAMP WHERE g.id IN :ids")
	void softDeleteByIds(@Param("ids") Set<Long> ids);
	
	@Query("SELECT p.code FROM CorePermission p WHERE p.appCode = :appCode")
	Set<String> findAllCodesByAppCode(@Param("appCode") String appCode);
	
	List<CorePermission> findAllByAppCode(String appCode);
	
	@Query(
			"""
					SELECT DISTINCT rp.permissionCode FROM CoreUserRole ur
					JOIN CoreRolePermission rp ON ur.appCode = rp.appCode AND ur.roleCode = rp.roleCode
					WHERE ur.username = :username AND ur.appCode = :appCode
					"""
	)
	Set<String> findAllCodesByUsernameAndAppCode(@Param("username") String username, @Param("appCode") String appCode);
	
	@Query("SELECT EXISTS(SELECT 1 FROM CoreUserRole ur WHERE ur.username = :username AND ur.appCode = 'SYSTEM' AND ur.roleCode = 'ROLE_SUPER_ADMIN')")
	boolean isSuperAdmin(@Param("username") String username);
	
}
