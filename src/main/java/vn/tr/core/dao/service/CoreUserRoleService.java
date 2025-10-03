package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.tr.core.dao.model.CoreRole;
import vn.tr.core.dao.model.CoreUserRole;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface cho Service Layer của CoreUserRole.
 * Cung cấp các "viên gạch" để tầng Business xây dựng các nghiệp vụ phức tạp.
 *
 * @author tyran8x
 * @version 2.0
 */
public interface CoreUserRoleService {
	
	// --- Hỗ trợ AssociationSyncHelper ---
	List<CoreUserRole> findByUsernameAndAppCodeIncludingDeleted(String username, String appCode);
	
	CoreUserRole save(CoreUserRole coreUserRole);
	
	void deleteById(Long id);
	
	JpaRepository<CoreUserRole, Long> getRepository();
	
	// --- Truy vấn nghiệp vụ ---
	Set<String> findActiveRoleCodesByUsernameAndAppCode(String username, String appCode);
	
	Set<String> findAllActiveRoleCodesByUsername(String username);
	
	/**
	 * Lấy danh sách role code đang hoạt động cho một tập hợp người dùng trong một app cụ thể.
	 */
	Map<String, Set<String>> findActiveRoleCodesForUsersInApp(Collection<String> usernames, String appCode);
	
	/**
	 * Lấy danh sách role code đang hoạt động cho một tập hợp người dùng trên tất cả các app.
	 */
	Map<String, Set<String>> findAllActiveRoleCodesForUsers(Collection<String> usernames);
	
	// --- Kiểm tra ràng buộc ---
	
	/**
	 * Kiểm tra xem một CoreRole cụ thể có đang được sử dụng (gán cho user) hay không.
	 *
	 * @param role Đối tượng CoreRole cần kiểm tra.
	 *
	 * @return true nếu đang được sử dụng, ngược lại false.
	 */
	boolean isRoleInUse(CoreRole role);
	
	boolean isSuperAdmin(String username);
	
}
