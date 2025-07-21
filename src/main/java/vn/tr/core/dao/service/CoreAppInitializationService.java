package vn.tr.core.dao.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.enums.TagCode;
import vn.tr.common.core.enums.UserType;
import vn.tr.core.dao.model.CoreApp;
import vn.tr.core.dao.model.CoreRole;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.dao.model.CoreUserType;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreAppInitializationService {
	
	// --- Constants ---
	private static final String SYSTEM_APP_CODE = "SYSTEM";
	private static final String NON_DELETABLE_TAG = "NON_DELETABLE"; // Mã tag
	// --- Dependencies ---
	private final CoreAppService coreAppService;
	private final CoreRoleService coreRoleService;
	private final CoreUserService coreUserService;
	private final CoreUserAppService coreUserAppService;
	// Bỏ CoreTagService nếu bạn chọn không dùng hệ thống Tagging phức tạp
	// private final CoreTagService tagService;
	private final CoreUserRoleService coreUserRoleService;
	private final CoreUserTypeService coreUserTypeService;
	private final CoreTagService coreTagService;
	
	@PostConstruct
	@Transactional
	public void init() {
		log.info("Bắt đầu quá trình khởi tạo dữ liệu hệ thống...");
		initializeSystemDefaults();
		
		// Bạn có thể gọi hàm khởi tạo cho các app cụ thể ở đây nếu cần
		// Ví dụ: khởi tạo dữ liệu mẫu cho app Sở Y Tế
		initializeSpecificApp("syt", "Hệ thống Sở Y Tế", Map.of("ROLE_SYT_DOCTOR", "Bác sĩ", "ROLE_SYT_NURSE", "Điều dưỡng"),
				"syt_admin");
		
		log.info("Hoàn tất quá trình khởi tạo dữ liệu.");
	}
	
	/**
	 * Khởi tạo các thành phần cốt lõi, toàn cục của hệ thống.
	 */
	public void initializeSystemDefaults() {
		log.info("Khởi tạo các thành phần hệ thống chung (SYSTEM)...");
		// 1. Tạo App "SYSTEM" - là chủ sở hữu của các thực thể toàn cục
		CoreApp systemApp = coreAppService.findOrCreate(SYSTEM_APP_CODE, "Hệ thống Chung");
		
		// 2. Tạo các Loại Người dùng (User Type) cố định
		coreUserTypeService.findOrCreate(UserType.INTERNAL.getUserType(), "Người dùng Nội bộ");
		coreUserTypeService.findOrCreate(UserType.EXTERNAL.getUserType(), "Người dùng Ngoài");
		coreUserTypeService.findOrCreate(UserType.PARTNER.getUserType(), "Đối tác Tích hợp");
		
		coreTagService.assignTag(UserType.INTERNAL.getUserType(), CoreUserType.class.getName(), TagCode.NON_DELETABLE.getCode());
		coreTagService.assignTag(UserType.EXTERNAL.getUserType(), CoreUserType.class.getName(), TagCode.NON_DELETABLE.getCode());
		coreTagService.assignTag(UserType.PARTNER.getUserType(), CoreUserType.class.getName(), TagCode.NON_DELETABLE.getCode());
		
		// 3. Tạo các Vai trò (Role) toàn cục
		CoreRole superAdminRole = coreRoleService.findOrCreate(systemApp, "ROLE_SUPER_ADMIN", "Quản trị viên Cấp cao nhất");
		CoreRole baseUserRole = coreRoleService.findOrCreate(systemApp, "ROLE_USER", "Người dùng Cơ bản");
		
		// 4. (Tùy chọn) Gắn tag bảo vệ cho vai trò quan trọng
		coreTagService.assignTag(superAdminRole.getCode(), CoreRole.class.getName(), TagCode.NON_DELETABLE.getCode());
		coreTagService.assignTag(baseUserRole.getCode(), CoreRole.class.getName(), TagCode.NON_DELETABLE.getCode());
		
		// 5. Tạo tài khoản 'root' - tài khoản quyền lực nhất
		CoreUser rootUser = coreUserService.findOrCreate("root", "Root Super Admin", "root@system.local", "Admin@123");
		
		// 6. Gán cho 'root' quyền truy cập app SYSTEM và vai trò SUPER_ADMIN
		coreUserAppService.assignUserToApp(rootUser.getUsername(), systemApp.getCode(), UserType.INTERNAL.getUserType());
		coreUserRoleService.assignRoleToUserInApp(rootUser.getUsername(), systemApp.getCode(), superAdminRole.getCode());
	}
	
	@Transactional
	public void initializeSpecificApp(String appCode, String appName, Map<String, String> customRoles, String adminUsername) {
		log.info("Khởi tạo dữ liệu cho ứng dụng: {}...", appCode);
		// 1. Tạo App
		CoreApp app = coreAppService.findOrCreate(appCode, appName);
		String upperAppCode = app.getCode().toUpperCase();
		
		// 2. Tạo các vai trò mặc định cho App (ADMIN, USER)
		String adminRoleCode = "ROLE_" + upperAppCode + "_ADMIN";
		String userRoleCode = "ROLE_" + upperAppCode + "_USER";
		CoreRole appAdminRole = coreRoleService.findOrCreate(app, adminRoleCode, "Quản trị viên " + app.getName());
		coreRoleService.findOrCreate(app, userRoleCode, "Người dùng " + app.getName());
		
		// 3. Tạo các vai trò tùy chỉnh khác nếu có
		for (Map.Entry<String, String> entry : customRoles.entrySet()) {
			coreRoleService.findOrCreate(app, entry.getKey(), entry.getValue());
		}
		
		// 4. Tạo tài khoản admin cho App
		CoreUser appAdminUser = coreUserService.findOrCreate(adminUsername, "Admin " + app.getName(), adminUsername + "@system.local", "Admin@123");
		
		// 5. Gán quyền truy cập App và vai trò Admin cho tài khoản admin
		coreUserAppService.assignUserToApp(appAdminUser.getUsername(), app.getCode(), UserType.INTERNAL.getUserType());
		coreUserRoleService.assignRoleToUserInApp(appAdminUser.getUsername(), app.getCode(), appAdminRole.getCode());
	}
}
