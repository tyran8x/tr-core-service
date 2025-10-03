package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.tr.core.dao.model.CoreUserApp;

import java.util.*;

/**
 * Interface cho Service Layer của CoreUserApp.
 * Cung cấp các "viên gạch" để tầng Business xây dựng các nghiệp vụ phức tạp.
 *
 * @author tyran8x
 * @version 2.0
 */
public interface CoreUserAppService {
	
	List<CoreUserApp> findByUsernameIncludingDeleted(String username);
	
	CoreUserApp save(CoreUserApp coreUserApp);
	
	void deleteById(Long id);
	
	JpaRepository<CoreUserApp, Long> getRepository();
	
	List<CoreUserApp> findByUsername(String username);
	
	Set<String> findActiveAppCodesByUsername(String username);
	
	boolean isUserInApp(String username, String appCode);
	
	Optional<CoreUserApp> findByUsernameAndAppCode(String username, String appCode);
	
	/**
	 * Lấy danh sách app code đang hoạt động cho một tập hợp người dùng.
	 * Tối ưu cho việc hiển thị danh sách, tránh N+1 query.
	 *
	 * @param usernames Collection các username cần truy vấn.
	 *
	 * @return Một Map với key là username và value là Set các app code.
	 */
	Map<String, Set<String>> findActiveAppCodesForUsers(Collection<String> usernames);
	
	// --- Các phương thức kiểm tra ràng buộc ---
	
	/**
	 * Kiểm tra xem một App có đang được sử dụng (gán cho user) hay không.
	 *
	 * @param appCode Mã của ứng dụng.
	 *
	 * @return true nếu đang được sử dụng, ngược lại false.
	 */
	boolean isAppInUse(String appCode);
	
	/**
	 * Kiểm tra xem một UserType có đang được sử dụng hay không.
	 *
	 * @param userTypeCode Mã của loại người dùng.
	 *
	 * @return true nếu đang được sử dụng, ngược lại false.
	 */
	boolean isUserTypeInUse(String userTypeCode);
	
	List<CoreUserApp> findActiveByUsername(String username);
}
