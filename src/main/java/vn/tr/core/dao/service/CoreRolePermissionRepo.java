package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreRolePermission;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Repository interface cho thực thể CoreRolePermission.
 * Đã cập nhật để xử lý logic dựa trên role_code, permission_code.
 *
 * @author tyran8x
 * @version 2.1
 */
@Repository
public interface CoreRolePermissionRepo extends JpaRepository<CoreRolePermission, Long>, JpaSpecificationExecutor<CoreRolePermission> {
	
	/**
	 * Tìm tất cả các bản ghi gán quyền cho một vai trò, BAO GỒM CẢ ĐÃ BỊ XÓA MỀM.
	 * Cần thiết cho logic đồng bộ hóa (AssociationSyncHelper).
	 */
	@Query("SELECT rp FROM CoreRolePermission rp WHERE rp.roleCode = :roleCode AND rp.appCode = :appCode")
	List<CoreRolePermission> findAllByRoleCodeAndAppCodeIncludingDeleted(@Param("roleCode") String roleCode, @Param("appCode") String appCode);
	
	/**
	 * Tìm tất cả các mã quyền (permission codes) đang hoạt động được gán cho một danh sách các vai trò (role codes).
	 */
	@Query("SELECT rp.permissionCode FROM CoreRolePermission rp WHERE rp.roleCode IN :roleCodes AND rp.appCode = :appCode")
	Set<String> findPermissionCodesByRoleCodesAndAppCode(@Param("roleCodes") Collection<String> roleCodes, @Param("appCode") String appCode);
	
	@Query("SELECT rp.permissionCode FROM CoreRolePermission rp WHERE rp.roleCode IN :roleCodes AND rp.appCode IN :appCodes")
	Set<String> findPermissionCodesByRoleCodesAndAppCodes(@Param("roleCodes") Collection<String> roleCodes, @Param("appCodes") Set<String> appCodes);
	
	boolean existsByRoleCodeAndAppCode(String roleCode, String appCode);
	
	boolean existsByPermissionCodeAndAppCode(String permissionCode, String appCode);
	
	@Query("SELECT crp FROM CoreRolePermission crp WHERE crp.appCode = :appCode AND crp.roleCode IN :roleCodes")
	List<CoreRolePermission> findActiveByRoleCodesAndAppCode(@Param("roleCodes") Set<String> roleCodes, @Param("appCode") String appCode);
	
	@Query("SELECT DISTINCT rp.permissionCode FROM CoreRolePermission rp WHERE rp.roleCode IN :roleCodes")
	Set<String> findPermissionCodesByRoleCodes(@Param("roleCodes") Collection<String> roleCodes);
}
