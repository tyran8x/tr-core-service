package vn.tr.core.security.service.strategy;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.constant.Constants;
import vn.tr.common.core.domain.model.LoginBody;
import vn.tr.common.core.domain.model.LoginUser;
import vn.tr.common.core.domain.model.PasswordLoginBody;
import vn.tr.common.core.domain.model.RegisterBody;
import vn.tr.common.core.enums.LoginType;
import vn.tr.common.core.exception.ServiceException;
import vn.tr.common.core.exception.user.UserException;
import vn.tr.common.core.utils.MessageUtils;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.dao.model.CoreUserApp;
import vn.tr.core.dao.service.CoreUserAppService;
import vn.tr.core.dao.service.CoreUserService;
import vn.tr.core.data.LoginResult;
import vn.tr.core.security.service.IAuthStrategy;

import java.util.Optional;

/**
 * Lớp cha trừu tượng chứa logic chung cho các chiến lược xác thực bằng mật khẩu.
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED) // Constructor cho các lớp con
public abstract class BasePasswordStrategy implements IAuthStrategy {
	
	// Sử dụng protected để các lớp con có thể truy cập
	protected final CoreUserService coreUserService;
	protected final CoreUserAppService coreUserAppService;
	
	@Override
	public LoginResult performLogin(LoginBody loginBody, String appCode) {
		if (!(loginBody instanceof PasswordLoginBody passwordBody)) {
			throw new ServiceException("Dữ liệu không hợp lệ cho grant type password.");
		}
		
		// Bước tiền xử lý (ví dụ: validate captcha) sẽ do lớp con quyết định
		preLoginValidate(passwordBody);
		
		String username = passwordBody.getUsername();
		String password = passwordBody.getPassword();
		if (appCode == null || appCode.isBlank()) {
			appCode = passwordBody.getAppCode();
		}
		
		CoreUser coreUser = coreUserService.findFirstByUsernameIgnoreCase(username)
				.orElseThrow(() -> new UserException("user.not.exists", username));
		
		coreUserService.checkLogin(LoginType.PASSWORD, username, () -> !BCrypt.checkpw(password, coreUser.getHashedPassword()));
		
		if (StrUtil.isNotEmpty(appCode)) {
			CoreUserApp userAppAccess = coreUserAppService.findByUsernameAndAppCode(username, appCode)
					.orElseThrow(() -> new UserException("user.app.access.denied"));
			
			coreUserService.checkUserAppStatus(userAppAccess);
		}
		
		LoginUser loginUser = coreUserService.buildLoginUser(coreUser);
		
		SaLoginParameter saLoginParameter = createLoginParameter(loginUser);
		saLoginParameter.setExtra(LoginHelper.APP_CODE, appCode);
		
		LoginHelper.login(loginUser, saLoginParameter);
		
		return createLoginResult();
	}
	
	/**
	 * Lớp con có thể override để thêm logic validate trước khi đăng nhập (ví dụ: captcha).
	 */
	protected void preLoginValidate(PasswordLoginBody passwordBody) {
		// Mặc định không làm gì
	}
	
	private SaLoginParameter createLoginParameter(LoginUser loginUser) {
		SaLoginParameter saLoginParameter = new SaLoginParameter();
//		saLoginParameter.setDeviceType(coreClientData.getDeviceType());
//		saLoginParameter.setTimeout(coreClientData.getTimeout() != null ? coreClientData.getTimeout() : 604800);
//		saLoginParameter.setActiveTimeout(coreClientData.getActiveTimeout() != null ? coreClientData.getTimeout() : 3600);
//		saLoginParameter.setExtra(LoginHelper.CLIENT_KEY, coreClientData.getClientId());
		saLoginParameter.setExtra(LoginHelper.USER_KEY, loginUser.getUserId());
		return saLoginParameter;
	}
	
	private LoginResult createLoginResult() {
		LoginResult loginResult = new LoginResult();
		loginResult.setAccessToken(StpUtil.getTokenValue());
		loginResult.setExpireIn(StpUtil.getTokenTimeout());
		loginResult.setTokenType(StpUtil.getTokenName());
		return loginResult;
	}
	
	// --- CÁC PHƯƠNG THỨC HELPER ---
	
	@Override
	@Transactional
	public void performRegistration(RegisterBody registerBody) {
		// Bước tiền xử lý (ví dụ: validate captcha)
		preRegisterValidate(registerBody);
		
		String username = registerBody.getUsername();
		String password = registerBody.getPassword();
		String appCode = registerBody.getAppCode();
		if (appCode == null || appCode.isBlank()) {
			throw new ServiceException("App code là bắt buộc khi đăng ký.");
		}
		
		if (coreUserService.existsByUsernameIgnoreCase(username)) {
			throw new UserException("user.register.save.error", username);
		}
		
		try {
			CoreUser coreUser = new CoreUser();
			coreUser.setUsername(username);
			coreUser.setHashedPassword(BCrypt.hashpw(password));
			coreUserService.save(coreUser);
			
			CoreUserApp firstAccess = new CoreUserApp();
			firstAccess.setUsername(username);
			firstAccess.setAppCode(appCode);
			firstAccess.setUserTypeCode(Optional.ofNullable(registerBody.getUserTypeCode()).orElse("EXTERNAL"));
			coreUserAppService.save(firstAccess);
			
		} catch (Exception e) {
			log.error("Lỗi khi đăng ký người dùng '{}': {}", username, e.getMessage(), e);
			throw new UserException("user.register.error");
		}
		
		coreUserService.recordLoginInfo(username, Constants.REGISTER, MessageUtils.message("user.register.success"));
	}
	
	/**
	 * Lớp con có thể override để thêm logic validate trước khi đăng ký (ví dụ: captcha).
	 */
	protected void preRegisterValidate(RegisterBody registerBody) {
		// Mặc định không làm gì
	}
}
