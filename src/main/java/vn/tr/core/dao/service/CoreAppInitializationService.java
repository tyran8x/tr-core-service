package vn.tr.core.dao.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.common.core.enums.UserType;
import vn.tr.core.business.CoreAppBusiness;
import vn.tr.core.business.CoreRoleBusiness;
import vn.tr.core.business.CoreUserBusiness;
import vn.tr.core.business.CoreUserTypeBusiness;
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
	
	private final CoreAppBusiness coreAppBusiness;
	private final CoreRoleBusiness coreRoleBusiness;
	private final CoreUserBusiness coreUserBusiness;
	private final CoreUserTypeBusiness coreUserTypeBusiness;
	
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
				CoreAppData.builder().code("SYSTEM").name("Hệ thống Chung").status(LifecycleStatus.ACTIVE).build(), true);
		
		// 2. Tạo các Loại Người dùng (User Type)
		upsertUserType(systemApp.getCode(), UserType.INTERNAL.getCode(), "Người dùng Nội bộ");
		upsertUserType(systemApp.getCode(), UserType.EXTERNAL.getCode(), "Người dùng Ngoài");
		upsertUserType(systemApp.getCode(), UserType.PARTNER.getCode(), "Đối tác Tích hợp");
		
		// 3. Tạo các Vai trò (Role) toàn cục
		// Tham số appCodeContext là null vì chúng ta đang thao tác trên app SYSTEM với quyền Super Admin
		upsertRole("ROLE_SUPER_ADMIN", systemApp.getCode(), "Quản trị viên Cấp cao nhất");
		upsertRole("ROLE_USER", systemApp.getCode(), "Người dùng Cơ bản");
		
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
		CoreAppData coreAppData = coreAppBusiness.create(CoreAppData.builder().code(appCode).name(appName).status(LifecycleStatus.ACTIVE).build(),
				true);
		String upperAppCode = coreAppData.getCode().toUpperCase();
		
		// 2. Tạo các vai trò
		String adminRoleCode = "ROLE_" + upperAppCode + "_ADMIN";
		upsertRole(adminRoleCode, "Quản trị viên " + coreAppData.getName(), coreAppData.getCode());
		upsertRole("ROLE_" + upperAppCode + "_USER", "Người dùng " + coreAppData.getName(), coreAppData.getCode());
		
		for (Map.Entry<String, String> entry : customRoles.entrySet()) {
			upsertRole(entry.getKey(), entry.getValue(), coreAppData.getCode());
		}
		
		// 3. Tạo và cấu hình tài khoản admin cho App
		CoreUserData appAdminData = CoreUserData.builder()
				.username(adminUsername)
				.fullName("Admin " + coreAppData.getName())
				.email(adminUsername + "@system.local")
				.password("Admin@123")
				.status(LifecycleStatus.ACTIVE)
				.userTypeCode(UserType.INTERNAL.getCode())
				.apps(Set.of(coreAppData.getCode()))
				.roles(Set.of(adminRoleCode))
				.build();
		
		// Gọi CoreUserBusiness.create. Super Admin (context null) đang tạo user cho một app cụ thể.
		coreUserBusiness.create(appAdminData, null);
	}
	
	// --- Private Helper Methods for Upserting ---
	
	private void upsertUserType(String appCodeContext, String code, String name) {
		CoreUserTypeData data = CoreUserTypeData.builder().code(code).name(name).status(LifecycleStatus.ACTIVE).build();
		coreUserTypeBusiness.create(data, appCodeContext);
	}
	
	private void upsertRole(String appCodeContext, String code, String name) {
		log.info("AppCode: {}, code: {}, name : {}", appCodeContext, code, name);
		CoreRoleData data = CoreRoleData.builder().code(code).name(name).status(LifecycleStatus.ACTIVE).appCode(appCodeContext).build();
		coreRoleBusiness.create(data, appCodeContext);
	}
}
