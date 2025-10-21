package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CorePermission;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface CorePermissionRepo extends JpaRepository<CorePermission, Long>, JpaSpecificationExecutor<CorePermission> {
	
	boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, @Nullable String appCode);
	
	boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, @Nullable String appCode);
	
	boolean existsByCodeIgnoreCaseAndAppCode(String code, @Nullable String appCode);
	
	boolean existsByNameIgnoreCaseAndAppCode(String name, @Nullable String appCode);
	
	/**
	 * Tìm kiếm Permission theo code và appCode, BAO GỒM CẢ BẢN GHI ĐÃ BỊ XÓA MỀM.
	 * Sắp xếp để ưu tiên bản ghi active và được cập nhật gần nhất.
	 */
	@Query("SELECT p FROM CorePermission p WHERE p.code = :code AND p.appCode = :appCode ORDER BY p.deletedAt ASC NULLS FIRST, p.updatedAt DESC")
	List<CorePermission> findAllByCodeAndAppCodeIncludingDeletedSorted(@Param("code") String code, @Param("appCode") String appCode);
	
	List<CorePermission> findAllByAppCode(String appCode);
	
	List<CorePermission> findAllByIdIn(Collection<Long> ids);
	
	Set<String> findAllCodesByAppCode(String appCode);
	
	boolean existsByModuleId(Long moduleId);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CorePermission p SET p.deletedAt = CURRENT_TIMESTAMP WHERE p.id IN :ids")
	void softDeleteByIds(@Param("ids") Collection<Long> ids);
	
	@Query(
			"""
						SELECT DISTINCT rp.permissionCode FROM CoreUserRole ur
						JOIN CoreRolePermission rp ON ur.appCode = rp.appCode AND ur.roleCode = rp.roleCode
						WHERE ur.username = :username AND ur.appCode = :appCode
					"""
	)
	Set<String> findAllCodesByUsernameAndAppCode(@Param("username") String username, @Param("appCode") String appCode);
	
	@Query(
			"""
						SELECT DISTINCT rp.permissionCode FROM CoreUserRole ur
						JOIN CoreRolePermission rp ON ur.appCode = rp.appCode AND ur.roleCode = rp.roleCode
						WHERE ur.username = :username AND ur.appCode IN :appCodes
					"""
	)
	Set<String> findAllCodesByUsernameAndAppCodes(@Param("username") String username, @Param("appCodes") Set<String> appCodes);
	
	@Query("SELECT EXISTS(SELECT 1 FROM CoreUserRole ur WHERE ur.username = :username AND ur.appCode = 'SYSTEM' AND ur.roleCode = 'ROLE_SUPER_ADMIN')")
	boolean isSuperAdmin(@Param("username") String username);
	
	List<CorePermission> findAllByAppCodeAndCodeIn(String appCode, Collection<String> codes);
}
