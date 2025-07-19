package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreRolePermission;

import java.util.List;

@Repository
public interface CoreRolePermissionRepo extends JpaRepository<CoreRolePermission, Long>, JpaSpecificationExecutor<CoreRolePermission> {
	
	@Query("SELECT rp FROM CoreRolePermission rp WHERE rp.roleCode = :roleCode AND rp.appCode = :appCode")
	List<CoreRolePermission> findAllByRoleCodeAndAppCodeEvenIfDeleted(@Param("roleCode") String roleCode, @Param("appCode") String appCode);
	
}
