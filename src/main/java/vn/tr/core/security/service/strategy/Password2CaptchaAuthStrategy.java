package vn.tr.core.security.service.strategy;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.tr.common.core.constant.Constants;
import vn.tr.common.core.constant.GlobalConstants;
import vn.tr.common.core.domain.model.LoginBody;
import vn.tr.common.core.domain.model.LoginUser;
import vn.tr.common.core.domain.model.PasswordLoginBody;
import vn.tr.common.core.domain.model.RegisterBody;
import vn.tr.common.core.enums.LoginType;
import vn.tr.common.core.enums.UserType;
import vn.tr.common.core.exception.ServiceException;
import vn.tr.common.core.exception.user.CaptchaException;
import vn.tr.common.core.exception.user.CaptchaExpireException;
import vn.tr.common.core.exception.user.UserException;
import vn.tr.common.core.utils.MessageUtils;
import vn.tr.common.core.utils.StringUtils;
import vn.tr.common.core.utils.ValidatorUtils;
import vn.tr.common.redis.utils.RedisUtils;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.common.web.config.properties.CaptchaProperties;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.dao.service.CoreUserService;
import vn.tr.core.data.CoreClientData;
import vn.tr.core.data.LoginResult;
import vn.tr.core.security.service.IAuthStrategy;

import java.util.Optional;

@Slf4j
@Service("password2captcha" + IAuthStrategy.BASE_NAME)
@RequiredArgsConstructor
public class Password2CaptchaAuthStrategy implements IAuthStrategy {
	
	private final CaptchaProperties captchaProperties;
	private final CoreUserService coreUserService;
	
	@Override
	public LoginResult login(LoginBody loginBody, CoreClientData coreClientData) {
		if (!(loginBody instanceof PasswordLoginBody passwordBody)) {
			throw new ServiceException("Dữ liệu không hợp lệ cho grant type password.");
		}
		ValidatorUtils.validate(passwordBody);
		
		String username = passwordBody.getUsername();
		String password = passwordBody.getPassword();
		String appCode = LoginHelper.getAppCode();
		String code = loginBody.getCode();
		String uuid = loginBody.getUuid();
		log.info("username: {}", username);
		log.info("password: {}", password);
		boolean captchaEnabled = captchaProperties.getEnable();
		if (captchaEnabled) {
			validateCaptcha(username, code, uuid);
		}
		CoreUser coreUser = loadUserByUsername(username);
		coreUserService.checkLogin(LoginType.PASSWORD, username, () -> !BCrypt.checkpw(password, coreUser.getHashedPassword()));
		LoginUser loginUser = coreUserService.buildLoginUser(coreUser, appCode);
		loginUser.setClientKey(coreClientData.getClientKey());
		loginUser.setDeviceType(coreClientData.getDeviceType());
		SaLoginParameter saLoginParameter = new SaLoginParameter();
		saLoginParameter.setDeviceType(coreClientData.getDeviceType());
		saLoginParameter.setTimeout(coreClientData.getTimeout() != null ? coreClientData.getTimeout() : 604800);
		saLoginParameter.setActiveTimeout(coreClientData.getActiveTimeout() != null ? coreClientData.getTimeout() : 3600);
		saLoginParameter.setExtra(LoginHelper.CLIENT_KEY, coreClientData.getClientId());
		saLoginParameter.setExtra(LoginHelper.USER_KEY, loginUser.getUserId());
		
		// generate token
		LoginHelper.login(loginUser, saLoginParameter);
		
		LoginResult loginResult = new LoginResult();
		loginResult.setAccessToken(StpUtil.getTokenValue());
		loginResult.setExpireIn(StpUtil.getTokenTimeout());
		loginResult.setClientId(coreClientData.getClientId());
		loginResult.setTokenType(StpUtil.getTokenName());
		return loginResult;
	}
	
	private void validateCaptcha(String username, String code, String uuid) {
		String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + StringUtils.blankToDefault(uuid, "");
		String captcha = RedisUtils.getCacheObject(verifyKey);
		RedisUtils.deleteObject(verifyKey);
		if (captcha == null) {
			coreUserService.recordLoginInfo(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
			throw new CaptchaExpireException();
		}
		if (!code.equalsIgnoreCase(captcha)) {
			coreUserService.recordLoginInfo(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error"));
			throw new CaptchaException();
		}
	}
	
	private CoreUser loadUserByUsername(String username) {
		Optional<CoreUser> optionalCoreUser = coreUserService.findFirstByUsernameIgnoreCase(username);
		if (optionalCoreUser.isEmpty()) {
			log.info("Login user: {} does not exist.", username);
			throw new UserException("user.not.exists", username);
		}
//		else if (!Boolean.TRUE.equals(optionalCoreUser.get().getIsEnabled())) {
//			log.info("Logged in user: {} has been deactivated.", username);
//			throw new UserException("user.blocked", username);
//		}
		return optionalCoreUser.get();
	}
	
	@Override
	public void register(RegisterBody registerBody, CoreClientData coreClientData) {
		
		assert registerBody != null;
		String username = registerBody.getUsername();
		String password = registerBody.getPassword();
		
		String userType = UserType.getUserType(registerBody.getUserType()).getUserType();
		
		String code = registerBody.getCode();
		String uuid = registerBody.getUuid();
		log.info("username register: {}", username);
		log.info("password register: {}", password);
		boolean captchaEnabled = captchaProperties.getEnable();
		if (captchaEnabled) {
			validateCaptcha(username, code, uuid);
		}
		CoreUser coreUser = new CoreUser();
//		coreUser.setUserName(username);
//		coreUser.setNickName(username);
//		coreUser.setEmail(username);
//		coreUser.setPassword(BCrypt.hashpw(password));
//		coreUser.setUserType(userType);
//		coreUser.setIsEnabled(true);
		
		boolean exist = coreUserService.existsByUsernameIgnoreCase(username);
		if (exist) {
			throw new UserException("user.register.save.error", username);
		}
		try {
			coreUserService.save(coreUser);
		} catch (Exception e) {
			throw new UserException("user.register.error");
		}
		
		coreUserService.recordLoginInfo(username, Constants.REGISTER, MessageUtils.message("user.register.success"));
	}
	
}
