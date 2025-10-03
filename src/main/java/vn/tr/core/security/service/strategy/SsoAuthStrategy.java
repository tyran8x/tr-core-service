package vn.tr.core.security.service.strategy;

import cn.hutool.crypto.digest.BCrypt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.tr.common.core.domain.model.LoginBody;
import vn.tr.common.core.domain.model.SsoLoginBody;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.dao.service.*;
import vn.tr.core.security.service.IAuthStrategy;

import java.util.UUID;

/**
 * Chiến lược xác thực cho luồng Single Sign-On.
 *
 * @author tyran8x
 * @version 1.0
 */
@Slf4j
@Service("sso" + IAuthStrategy.BASE_NAME)
public class SsoAuthStrategy extends BaseUserLookupStrategy {
	
	// private final SsoValidationService ssoValidationService; // Service để validate token từ IdP
	
	public SsoAuthStrategy(CoreUserService a, CoreUserAppService b, CoreUserRoleService c, CoreUserGroupService d, CoreRolePermissionService e) {
		super(a, b, c, d, e);
	}
	
	@Override
	protected void preLoginValidation(LoginBody loginBody) {
		SsoLoginBody ssoBody = (SsoLoginBody) loginBody;
		log.info("Validating SSO assertion for user: {}", ssoBody.getUsername());
		
		// **LOGIC QUAN TRỌNG:**
		// Gọi đến một service để xác thực token/assertion từ nhà cung cấp định danh (IdP).
		// Ví dụ: giải mã và kiểm tra chữ ký của SAML assertion hoặc OIDC id_token.
		// boolean isValid = ssoValidationService.validate(ssoBody.getAssertion());
		// if (!isValid) {
		//     throw new UserException("sso.assertion.invalid");
		// }
	}
	
	@Override
	protected String getUserIdentifier(LoginBody loginBody) {
		// Có thể là username hoặc email tùy thuộc vào thông tin IdP trả về
		return ((SsoLoginBody) loginBody).getUsername();
	}
	
	/**
	 * Override để triển khai Just-In-Time Provisioning.
	 */
	@Override
	protected CoreUser findOrProvisionUser(String identifier, LoginBody loginBody) {
		SsoLoginBody ssoBody = (SsoLoginBody) loginBody;
		
		return coreUserService.findByUsernameOrEmail(identifier)
				.orElseGet(() -> {
					// Nếu không tìm thấy người dùng, tự động tạo mới
					log.info("User '{}' not found. Provisioning a new account from SSO data.", identifier);
					
					CoreUser newUser = new CoreUser();
					newUser.setUsername(ssoBody.getUsername());
					newUser.setEmail(ssoBody.getEmail());
					newUser.setFullName(ssoBody.getFullName());
					// Mật khẩu có thể để null hoặc một giá trị ngẫu nhiên vì họ không đăng nhập bằng mật khẩu
					newUser.setHashedPassword(BCrypt.hashpw(UUID.randomUUID().toString()));
					
					// Gán các vai trò/nhóm mặc định dựa trên thông tin từ SSO (nếu có)
					// ...
					
					return coreUserService.save(newUser);
				});
	}
	
	@Override
	protected String getTargetAppCode(LoginBody loginBody) {
		return ((SsoLoginBody) loginBody).getAppCode();
	}
	
	@Override
	protected UserLookupContext validateAndGetContext(LoginBody loginBody) {
		// 1. Ép kiểu an toàn
		if (!(loginBody instanceof SsoLoginBody ssoBody)) {
			throw new ServiceException("Dữ liệu không hợp lệ cho grant type sso.");
		}
		
		// 2. Thực hiện validation đặc thù
		log.info("Validating SSO assertion for user: {}", ssoBody.getUsername());
		// ssoValidationService.validate(ssoBody.getAssertion());
		
		// 3. Trả về context
		return new UserLookupContext(
				ssoBody.getUsername(),      // userIdentifier
				ssoBody.getAppCode(),       // targetAppCode
				ssoBody                     // originalBody
		);
	}
}
