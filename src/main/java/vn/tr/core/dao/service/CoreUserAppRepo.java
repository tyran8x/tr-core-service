package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreUserApp;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Repository interface cho thực thể CoreUserApp.
 *
 * @author tyran8x
 * @version 2.0
 */
@Repository
public interface CoreUserAppRepo extends JpaRepository<CoreUserApp, Long>, JpaSpecificationExecutor<CoreUserApp> {
	
	// --- Các phương thức hỗ trợ AssociationSyncHelper (bao gồm cả đã xóa mềm) ---
	
	@Query("SELECT cua FROM CoreUserApp cua WHERE cua.username = :username")
	List<CoreUserApp> findAllByUsernameIncludingDeleted(@Param("username") String username);
	
	// --- Các phương thức truy vấn nghiệp vụ (chỉ lấy bản ghi active) ---
	
	List<CoreUserApp> findByUsername(String username);
	
	List<CoreUserApp> findByUsernameAndAppCode(String username, String appCode);
	
	boolean existsByUsernameAndAppCode(String username, String appCode);
	
	@Query("SELECT cua.appCode FROM CoreUserApp cua WHERE cua.username = :username AND cua.status = 'ACTIVE'")
	Set<String> findActiveAppCodesByUsername(@Param("username") String username);
	
	@Query("SELECT cua FROM CoreUserApp cua WHERE cua.username IN :usernames AND cua.status = 'ACTIVE'")
	List<CoreUserApp> findActiveByUsernamesIn(@Param("usernames") Collection<String> usernames);
	
	// --- Các phương thức kiểm tra ràng buộc ---
	
	boolean existsByAppCode(String appCode);
	
	boolean existsByUserTypeCode(String userTypeCode);
}
