package vn.tr.core.security.service.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.tr.common.core.domain.model.EmailLoginBody;
import vn.tr.common.core.domain.model.LoginBody;
import vn.tr.core.dao.service.*;
import vn.tr.core.security.service.IAuthStrategy;

@Slf4j
@Service("email" + IAuthStrategy.BASE_NAME)
public class EmailAuthStrategy extends BaseUserLookupStrategy {
	
	public EmailAuthStrategy(CoreUserService a, CoreUserAppService b, CoreUserRoleService c, CoreUserGroupService d, CoreRolePermissionService e) {
		super(a, b, c, d, e);
	}
	
	@Override
	protected void preLoginValidation(LoginBody loginBody) {
		EmailLoginBody emailBody = (EmailLoginBody) loginBody;
		log.info("Validating OTP for email: {}", emailBody.getEmail());
		validateOtp(emailBody.getEmail(), emailBody.getCode());
	}
	
	@Override
	protected String getUserIdentifier(LoginBody loginBody) {
		return ((EmailLoginBody) loginBody).getEmail();
	}
	
	@Override
	protected String getTargetAppCode(LoginBody loginBody) {
		return loginBody.getAppCode();
	}
	
	private void validateOtp(String email, String otpCode) {
		// ... logic validate OTP của bạn ...
		log.debug("OTP for {} validated successfully.", email);
	}
}
