package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.service.PermissionService;
import vn.tr.core.dao.model.CoreUser;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Lớp triển khai PermissionService, hỗ trợ đa ứng dụng và phân quyền theo ngữ cảnh.
 * Phiên bản này ưu tiên kiểm tra vai trò quản trị toàn cục một cách độc lập,
 * sau đó mới xử lý các quyền theo từng ứng dụng.
 *
 * @author tyran8x
 * @version 2.6 (True Global Admin Check & Full Implementation)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionServiceImpl implements PermissionService {
	
	// --- Constants ---
	private static final String NAMESPACE_SEPARATOR = ":";
	private static final String ALL_PERMISSIONS_WILDCARD = "*";
	/**
	 * Danh sách các vai trò quản trị toàn cục, có hiệu lực trên tất cả các ứng dụng.
	 * Nếu người dùng có một trong các vai trò này, họ sẽ nhận được quyền "*" (tất cả).
	 */
	private static final Set<String> GLOBAL_SUPER_ROLES = Set.of(
			"ROLE_SUPER_ADMIN",
			"ROLE_SUPER_DEV",
			"ROLE_ADMIN",
			"ROLE_DEV"
	                                                            );
	// --- Dependencies ---
	private final CoreUserService coreUserService;
	private final CoreUserRoleService coreUserRoleService;
	private final CoreUserGroupService coreUserGroupService;
	private final CoreRolePermissionService coreRolePermissionService;
	
	@Override
	@Transactional(readOnly = true)
	public Set<String> getRoleCodes(Long userId, Set<String> appCodes) {
		log.debug("Đang lấy vai trò cho userId {} trong app {}", userId, appCodes);
		return coreUserService.findById(userId)
				.map(CoreUser::getUsername)
				.map(username -> coreUserRoleService.findActiveRoleCodesByUsernameAndAppCodes(username, appCodes))
				.orElse(Collections.emptySet());
	}
	
	/**
	 * Lấy danh sách mã quyền của người dùng trong phạm vi các ứng dụng được chỉ định.
	 *
	 * @param userId   ID của người dùng.
	 * @param appCodes Một Set các mã ứng dụng để lọc quyền.
	 *
	 * @return Một Set chứa các mã quyền (có thể là "*", "appCode:*", hoặc "appCode:permission_code").
	 */
	@Override
	@Transactional(readOnly = true)
	public Set<String> getPermissionCodes(Long userId, Set<String> appCodes) {
		log.info("Đang lấy quyền hạn (v2.6) cho userId {} trong các ứng dụng {}", userId, appCodes);
		
		Optional<String> usernameOpt = coreUserService.findById(userId).map(CoreUser::getUsername);
		if (usernameOpt.isEmpty()) {
			log.warn("Không tìm thấy người dùng với userId: {}. Trả về quyền rỗng.", userId);
			return Collections.emptySet();
		}
		String username = usernameOpt.get();
		
		// BƯỚC 1: KIỂM TRA VAI TRÒ QUẢN TRỊ TOÀN CỤC (ƯU TIÊN CAO NHẤT)
		// Lấy TẤT CẢ các vai trò của người dùng trên toàn hệ thống, không lọc theo appCode.
		Set<String> allUserRolesGlobally = coreUserRoleService.findAllActiveRoleCodesByUsername(username);
		
		// Nếu người dùng có một trong các vai trò quản trị toàn cục, cấp quyền "*" và kết thúc ngay.
		if (!Collections.disjoint(allUserRolesGlobally, GLOBAL_SUPER_ROLES)) {
			log.info("User {} có vai trò quản trị toàn cục ({}). Cấp toàn quyền (*).", userId, allUserRolesGlobally);
			return Collections.singleton(ALL_PERMISSIONS_WILDCARD);
		}
		
		// BƯỚC 2: NẾU KHÔNG PHẢI QUẢN TRỊ TOÀN CỤC, XỬ LÝ THEO TỪNG appCode
		// Nếu không có appCode nào được cung cấp, không có quyền nào để trả về.
		if (appCodes.isEmpty()) {
			log.warn("User {} không phải quản trị toàn cục và không có appCode nào được cung cấp. Trả về quyền rỗng.", userId);
			return Collections.emptySet();
		}
		
		Set<String> finalNamespacedPermissions = new HashSet<>();
		for (String appCode : appCodes) {
			// Lấy các vai trò của người dùng chỉ trong phạm vi ứng dụng hiện tại
			Set<String> rolesInApp = coreUserRoleService.findActiveRoleCodesByUsernameAndAppCodes(username, Set.of(appCode));
			
			if (rolesInApp.isEmpty()) {
				continue; // Bỏ qua nếu người dùng không có vai trò nào trong app này
			}
			
			// Sinh ra các vai trò quản trị theo quy ước cho ứng dụng này
			Set<String> conventionSuperRolesForApp = generateSuperRolesForApp(appCode);
			
			// Kiểm tra xem người dùng có vai trò quản trị trong ứng dụng này không
			if (!Collections.disjoint(rolesInApp, conventionSuperRolesForApp)) {
				String appWildcardPermission = appCode + NAMESPACE_SEPARATOR + ALL_PERMISSIONS_WILDCARD;
				log.info("User {} có vai trò quản trị trong app {} (theo quy ước). Cấp quyền: {}", userId, appCode, appWildcardPermission);
				finalNamespacedPermissions.add(appWildcardPermission);
			} else {
				// Nếu không, lấy các quyền chi tiết từ các vai trò đó
				log.debug("User {} không có vai trò quản trị trong app {}. Lấy quyền chi tiết cho các vai trò: {}", userId, appCode, rolesInApp);
				Set<String> detailedPermissions = coreRolePermissionService.findPermissionCodesByRoleCodesAndAppCodes(rolesInApp, Set.of(appCode));
				
				// Gắn namespace (tiền tố appCode) cho từng quyền
				detailedPermissions.stream()
						.map(permission -> appCode + NAMESPACE_SEPARATOR + permission)
						.forEach(finalNamespacedPermissions::add);
			}
		}
		
		log.info("Tổng hợp quyền cho userId {}: {}", userId, finalNamespacedPermissions);
		return finalNamespacedPermissions;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Set<String> getGroupCodes(Long userId, Set<String> appCodes) {
		log.debug("Đang lấy nhóm cho userId {} trong app {}", userId, appCodes);
		return coreUserService.findById(userId)
				.map(CoreUser::getUsername)
				.map(username -> coreUserGroupService.findActiveGroupCodesByUsernameAndAppCodes(username, appCodes))
				.orElse(Collections.emptySet());
	}
	
	/**
	 * Sinh ra danh sách các tên vai trò quản trị (super roles) cho một app cụ thể dựa trên quy ước.
	 * Quy ước: ROLE_{APP_CODE_UPPERCASE}_ADMIN và ROLE_{APP_CODE_UPPERCASE}_DEV
	 *
	 * @param appCode Mã ứng dụng (ví dụ: "sgtvt")
	 *
	 * @return Một Set chứa các tên vai trò quản trị có thể có (ví dụ: {"ROLE_SGTVT_ADMIN", "ROLE_SGTVT_DEV"})
	 */
	private Set<String> generateSuperRolesForApp(String appCode) {
		if (appCode.isBlank()) {
			return Collections.emptySet();
		}
		String appCodeUpperCase = appCode.toUpperCase();
		return Set.of(
				"ROLE_" + appCodeUpperCase + "_ADMIN",
				"ROLE_" + appCodeUpperCase + "_DEV"
		             );
	}
}
