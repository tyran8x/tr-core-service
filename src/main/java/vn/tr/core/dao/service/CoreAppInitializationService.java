package vn.tr.core.dao.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.common.core.enums.UserType;
import vn.tr.core.business.*;
import vn.tr.core.data.dto.CoreAppData;
import vn.tr.core.data.dto.CoreRoleData;
import vn.tr.core.data.dto.CoreUserData;
import vn.tr.core.data.dto.CoreUserTypeData;

import java.util.Map;
import java.util.Set;

/**
 * Service chịu trách nhiệm khởi tạo dữ liệu nền tảng cho hệ thống khi khởi động. Lớp này đã được tối ưu để gọi đến các tầng Business, đảm bảo logic
 * được tái sử dụng và nhất quán với các thao tác API thông thường.
 *
 * @author tyran8x
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CoreAppInitializationService {
	
	// --- Dependencies (giờ đây là các lớp Business) ---
	private final CoreAppBusiness coreAppBusiness;
	private final CoreRoleBusiness coreRoleBusiness;
	private final CoreUserBusiness coreUserBusiness;
	private final CoreUserTypeBusiness coreUserTypeBusiness;
	private final CoreTagBusiness coreTagBusiness;
	// ... có thể cần CoreTagAssignmentService nếu logic assignTag không nằm trong Business
	
	/**
	 * Phương thức này được tự động gọi sau khi service được khởi tạo.
	 */
	@PostConstruct
	@Transactional
	public void init() {
		log.info("Bắt đầu quá trình khởi tạo dữ liệu hệ thống...");
		
		initializeSystemDefaults();
		
		initializeSpecificApp(
				"syt",
				"Hệ thống Sở Y Tế",
				Map.of("ROLE_SYT_DOCTOR", "Bác sĩ", "ROLE_SYT_NURSE", "Điều dưỡng"),
				"syt_admin"
		                     );
		
		log.info("Hoàn tất quá trình khởi tạo dữ liệu.");
	}
	
	/**
	 * Khởi tạo các thành phần cốt lõi, toàn cục của hệ thống.
	 */
	private void initializeSystemDefaults() {
		log.info("Khởi tạo các thành phần hệ thống chung (SYSTEM)...");
		
		// 1. Tạo App "SYSTEM"
		// Tầng Business đã có logic upsert, chúng ta chỉ cần gọi create.
		// Quyền Super Admin được giả định là true vì đây là tiến trình hệ thống.
		CoreAppData systemApp = coreAppBusiness.create(
				CoreAppData.builder().code("SYSTEM").name("Hệ thống Chung").status(LifecycleStatus.ACTIVE).build(),
				true // isSuperAdmin
		                                              );
		
		// 2. Tạo các Loại Người dùng (User Type)
		upsertUserType(UserType.INTERNAL.getCode(), "Người dùng Nội bộ");
		upsertUserType(UserType.EXTERNAL.getCode(), "Người dùng Ngoài");
		upsertUserType(UserType.PARTNER.getCode(), "Đối tác Tích hợp");
		
		// 3. Tạo các Vai trò (Role) toàn cục
		// Tham số appCodeContext là null vì chúng ta đang thao tác trên app SYSTEM với quyền Super Admin
		upsertRole(null, "ROLE_SUPER_ADMIN", "Quản trị viên Cấp cao nhất", "SYSTEM");
		upsertRole(null, "ROLE_USER", "Người dùng Cơ bản", "SYSTEM");
		
		// 4. Tạo và cấu hình tài khoản 'root'
		CoreUserData rootUserData = CoreUserData.builder()
				.username("root")
				.fullName("Root Super Admin")
				.email("root@system.local")
				.password("Admin@123")
				.status(LifecycleStatus.ACTIVE)
				.userTypeCode(UserType.INTERNAL.getCode())
				.apps(Set.of("SYSTEM")) // Gán vào app SYSTEM
				.roles(Set.of("ROLE_SUPER_ADMIN")) // Gán vai trò SUPER_ADMIN
				.build();
		
		// Gọi đến CoreUserBusiness.create. Tầng Business sẽ lo toàn bộ việc đồng bộ hóa.
		coreUserBusiness.create(rootUserData, null); // appCodeContext là null vì là Super Admin
	}
	
	/**
	 * Khởi tạo dữ liệu cho một ứng dụng cụ thể.
	 */
	private void initializeSpecificApp(String appCode, String appName, Map<String, String> customRoles, String adminUsername) {
		log.info("Khởi tạo dữ liệu cho ứng dụng: {}...", appCode);
		
		// 1. Tạo App
		CoreAppData app = coreAppBusiness.create(
				CoreAppData.builder().code(appCode).name(appName).status(LifecycleStatus.ACTIVE).build(),
				true // isSuperAdmin
		                                        );
		String upperAppCode = app.getCode().toUpperCase();
		
		// 2. Tạo các vai trò
		String adminRoleCode = "ROLE_" + upperAppCode + "_ADMIN";
		upsertRole(null, adminRoleCode, "Quản trị viên " + app.getName(), app.getCode());
		upsertRole(null, "ROLE_" + upperAppCode + "_USER", "Người dùng " + app.getName(), app.getCode());
		
		for (Map.Entry<String, String> entry : customRoles.entrySet()) {
			upsertRole(null, entry.getKey(), entry.getValue(), app.getCode());
		}
		
		// 3. Tạo và cấu hình tài khoản admin cho App
		CoreUserData appAdminData = CoreUserData.builder()
				.username(adminUsername)
				.fullName("Admin " + app.getName())
				.email(adminUsername + "@system.local")
				.password("Admin@123")
				.status(LifecycleStatus.ACTIVE)
				.userTypeCode(UserType.INTERNAL.getCode())
				.apps(Set.of(app.getCode())) // Gán vào app này
				.roles(Set.of(adminRoleCode)) // Gán vai trò Admin của app này
				.build();
		
		// Gọi CoreUserBusiness.create. Super Admin (context null) đang tạo user cho một app cụ thể.
		coreUserBusiness.create(appAdminData, null);
	}
	
	// --- Private Helper Methods for Upserting ---
	
	private void upsertUserType(String code, String name) {
		CoreUserTypeData data = CoreUserTypeData.builder().code(code).name(name).status(LifecycleStatus.ACTIVE).build();
		// Giả sử CoreUserTypeBusiness.create đã có logic upsert
		coreUserTypeBusiness.create(data, null); // isSuperAdmin
	}
	
	private void upsertRole(String appCodeContext, String code, String name, String appCodeForRole) {
		CoreRoleData data = CoreRoleData.builder().code(code).name(name).status(LifecycleStatus.ACTIVE).appCode(appCodeForRole).build();
		// Giả sử CoreRoleBusiness.create đã có logic upsert
		coreRoleBusiness.create(data, appCodeContext);
	}
}
