package vn.tr.core.security.service.strategy;

import lombok.extern.slf4j.Slf4j;
import vn.tr.common.core.domain.model.LoginBody;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.common.core.exception.user.UserException;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.dao.service.*;
import vn.tr.core.data.LoginResult;
import vn.tr.core.data.dto.CoreClientData;

/**
 * Lớp cha cho các chiến lược xác thực không dùng mật khẩu,
 * nhưng cần tìm người dùng dựa trên một thuộc tính (vd: email, username từ SSO).
 *
 * @author tyran8x
 * @version 1.0
 */
@Slf4j
public abstract class BaseUserLookupStrategy extends AbstractAuthStrategy {
	
	protected BaseUserLookupStrategy(CoreUserService coreUserService, CoreUserAppService coreUserAppService,
			CoreUserRoleService coreUserRoleService, CoreUserGroupService coreUserGroupService,
			CoreRolePermissionService coreRolePermissionService) {
		super(coreUserService, coreUserAppService, coreUserRoleService, coreUserGroupService, coreRolePermissionService);
	}
	
	@Override
	public final LoginResult performLogin(LoginBody loginBody, CoreClientData client, String appCodeFromHeader) {
		// 1. Thực hiện các bước xác thực đặc thù của lớp con (vd: validate OTP, validate SSO token)
		preLoginValidation(loginBody);
		
		// 2. Lấy định danh người dùng (username hoặc email) từ request body
		String userIdentifier = getUserIdentifier(loginBody);
		
		// 3. Tìm hoặc tự động tạo người dùng (Just-In-Time Provisioning)
		CoreUser user = findOrProvisionUser(userIdentifier, loginBody);
		
		// 4. Lấy appCode từ request body
		String targetAppCode = getTargetAppCode(loginBody);
		
		// 5. Tái sử dụng logic chung để hoàn tất quá trình đăng nhập
		return processLogin(user, client, targetAppCode);
	}
	
	/**
	 * Lớp con phải triển khai để cung cấp logic xác thực trước khi tìm người dùng.
	 * Ví dụ: kiểm tra OTP, kiểm tra token từ IdP.
	 */
	protected abstract void preLoginValidation(LoginBody loginBody);
	
	/**
	 * Lớp con phải triển khai để trích xuất định danh chính (username/email) từ LoginBody.
	 */
	protected abstract String getUserIdentifier(LoginBody loginBody);
	
	/**
	 * Tìm người dùng theo định danh. Nếu không tìm thấy, có thể tự động tạo mới (JIT Provisioning).
	 * Lớp con có thể override để thay đổi hành vi này.
	 */
	protected CoreUser findOrProvisionUser(String identifier, LoginBody loginBody) {
		// Mặc định: Tìm bằng email hoặc username. Ném lỗi nếu không tìm thấy.
		CoreUser user = coreUserService.findByUsernameOrEmail(identifier)
				.orElseThrow(() -> new UserException("user.sso.not.found", identifier));
		
		if (user.getStatus() != LifecycleStatus.ACTIVE) {
			throw new UserException("user.blocked", user.getUsername());
		}
		return user;
	}
	
	/**
	 * Lớp con phải triển khai để trích xuất appCode từ LoginBody.
	 */
	protected abstract String getTargetAppCode(LoginBody loginBody);
}
