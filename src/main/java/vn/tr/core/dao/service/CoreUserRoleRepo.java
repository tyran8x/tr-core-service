package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreUserRole;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Repository interface cho thực thể CoreUserRole.
 *
 * @author tyran8x
 * @version 2.0
 */
@Repository
public interface CoreUserRoleRepo extends JpaRepository<CoreUserRole, Long>, JpaSpecificationExecutor<CoreUserRole> {
	
	// --- Hỗ trợ AssociationSyncHelper ---
	@Query("SELECT cur FROM CoreUserRole cur WHERE cur.username = :username AND cur.appCode = :appCode")
	List<CoreUserRole> findAllByUsernameAndAppCodeIncludingDeleted(@Param("username") String username, @Param("appCode") String appCode);
	
	// --- Truy vấn nghiệp vụ (chỉ lấy bản ghi active) ---
	@Query("SELECT cur.roleCode FROM CoreUserRole cur WHERE cur.username = :username AND cur.appCode = :appCode")
	Set<String> findActiveRoleCodesByUsernameAndAppCode(@Param("username") String username, @Param("appCode") String appCode);
	
	@Query("SELECT cur.roleCode FROM CoreUserRole cur WHERE cur.username = :username")
	Set<String> findAllActiveRoleCodesByUsername(@Param("username") String username);
	
	@Query("SELECT cur FROM CoreUserRole cur WHERE cur.username IN :usernames AND cur.appCode = :appCode")
	List<CoreUserRole> findActiveForUsersInApp(@Param("usernames") Collection<String> usernames, @Param("appCode") String appCode);
	
	// --- Kiểm tra ràng buộc ---
	boolean existsByRoleCodeAndAppCode(String roleCode, String appCode);
	
	boolean existsByUsernameAndRoleCodeAndAppCode(String username, String roleCode, String appCode);
	
}
