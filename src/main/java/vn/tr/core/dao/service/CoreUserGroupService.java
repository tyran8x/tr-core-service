package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.tr.core.dao.model.CoreGroup;
import vn.tr.core.dao.model.CoreUserGroup;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CoreUserGroupService {
	
	List<CoreUserGroup> findByUsernameAndAppCodeIncludingDeleted(String username, String appCode);
	
	CoreUserGroup save(CoreUserGroup coreUserGroup);
	
	void deleteById(Long id);
	
	JpaRepository<CoreUserGroup, Long> getRepository();
	
	// --- Truy vấn nghiệp vụ ---
	List<CoreUserGroup> findByUsernameAndAppCode(String username, String appCode);
	
	Set<String> findActiveGroupCodesByUsernameAndAppCode(String username, String appCode);
	
	Set<String> findAllActiveGroupCodesByUsername(String username);
	
	/**
	 * Lấy danh sách group code đang hoạt động cho một tập hợp người dùng trong một app cụ thể. Tối ưu cho việc hiển thị danh sách, tránh N+1 query.
	 */
	Map<String, Set<String>> findActiveGroupCodesForUsersInApp(Collection<String> usernames, String appCode);
	
	/**
	 * Kiểm tra xem một CoreGroup cụ thể có đang được sử dụng (gán cho user) hay không.
	 *
	 * @param coreGroup Đối tượng CoreGroup cần kiểm tra.
	 *
	 * @return true nếu đang được sử dụng, ngược lại false.
	 */
	boolean isGroupInUse(CoreGroup coreGroup);
	
	/**
	 * BỔ SUNG: Lấy danh sách group code đang hoạt động cho một tập hợp người dùng trên tất cả các app. Tối ưu cho việc hiển thị danh sách của Super
	 * Admin.
	 *
	 * @param usernames Collection các username cần truy vấn.
	 *
	 * @return Một Map với key là username và value là Set các group code.
	 */
	Map<String, Set<String>> findAllActiveGroupCodesForUsers(Collection<String> usernames);
	
}
