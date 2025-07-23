package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreUserGroup;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Repository interface cho thực thể CoreUserGroup.
 *
 * @author tyran8x
 * @version 2.0
 */
@Repository
public interface CoreUserGroupRepo extends JpaRepository<CoreUserGroup, Long>, JpaSpecificationExecutor<CoreUserGroup> {
	
	// --- Hỗ trợ AssociationSyncHelper (bao gồm cả đã xóa mềm) ---
	@Query("SELECT cug FROM CoreUserGroup cug WHERE cug.username = :username AND cug.appCode = :appCode")
	List<CoreUserGroup> findAllByUsernameAndAppCodeIncludingDeleted(@Param("username") String username, @Param("appCode") String appCode);
	
	// --- Truy vấn nghiệp vụ (chỉ lấy bản ghi active) ---
	List<CoreUserGroup> findByUsernameAndAppCode(String username, String appCode);
	
	@Query("SELECT cug.groupCode FROM CoreUserGroup cug WHERE cug.username = :username AND cug.appCode = :appCode")
	Set<String> findActiveGroupCodesByUsernameAndAppCode(@Param("username") String username, @Param("appCode") String appCode);
	
	@Query("SELECT cug.groupCode FROM CoreUserGroup cug WHERE cug.username = :username")
	Set<String> findAllActiveGroupCodesByUsername(@Param("username") String username);
	
	@Query("SELECT cug FROM CoreUserGroup cug WHERE cug.username IN :usernames AND cug.appCode = :appCode")
	List<CoreUserGroup> findActiveForUsersInApp(@Param("usernames") Collection<String> usernames, @Param("appCode") String appCode);
	
	/**
	 * Kiểm tra xem có bất kỳ người dùng nào được gán vào một group cụ thể (thông qua code và appCode) hay không.
	 * Đây là phương thức cốt lõi để kiểm tra ràng buộc xóa.
	 *
	 * @param groupCode Mã của nhóm.
	 * @param appCode   Mã của ứng dụng.
	 *
	 * @return true nếu có ít nhất một user thuộc group, ngược lại false.
	 */
	boolean existsByGroupCodeAndAppCode(String groupCode, String appCode);
}
