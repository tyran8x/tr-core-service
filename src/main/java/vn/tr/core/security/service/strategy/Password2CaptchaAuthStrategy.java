package vn.tr.core.security.service.strategy;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.tr.common.core.constant.Constants;
import vn.tr.common.core.constant.GlobalConstants;
import vn.tr.common.core.domain.model.LoginUser;
import vn.tr.common.core.domain.model.PasswordLoginBody;
import vn.tr.common.core.domain.model.RegisterBody;
import vn.tr.common.core.enums.LoginType;
import vn.tr.common.core.enums.UserType;
import vn.tr.common.core.exception.user.CaptchaException;
import vn.tr.common.core.exception.user.CaptchaExpireException;
import vn.tr.common.core.exception.user.UserException;
import vn.tr.common.core.utils.MessageUtils;
import vn.tr.common.core.utils.StringUtils;
import vn.tr.common.core.utils.ValidatorUtils;
import vn.tr.common.json.utils.JsonUtils;
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
	public LoginResult login(String body, CoreClientData coreClientData) {
		PasswordLoginBody loginBody = JsonUtils.parseObject(body, PasswordLoginBody.class);
		log.info("loginBody: {}", loginBody);
		ValidatorUtils.validate(loginBody);
		log.info("pass loginBody");
		assert loginBody != null;
		
		String userName = loginBody.getUserName();
		String password = loginBody.getPassword();
		String code = loginBody.getCode();
		String uuid = loginBody.getUuid();
		log.info("userName: {}", userName);
		log.info("password: {}", password);
		boolean captchaEnabled = captchaProperties.getEnable();
		if (captchaEnabled) {
			validateCaptcha(userName, code, uuid);
		}
		CoreUser coreUser = loadUserByUsername(userName);
		coreUserService.checkLogin(LoginType.PASSWORD, userName, () -> !BCrypt.checkpw(password, coreUser.getPassword()));
		LoginUser loginUser = coreUserService.buildLoginUser(coreUser);
		loginUser.setClientKey(coreClientData.getClientKey());
		loginUser.setDeviceType(coreClientData.getDeviceType());
		SaLoginModel saLoginModel = new SaLoginModel();
		saLoginModel.setDevice(coreClientData.getDeviceType());
		saLoginModel.setTimeout(coreClientData.getTimeout() != null ? coreClientData.getTimeout() : 604800);
		saLoginModel.setActiveTimeout(coreClientData.getActiveTimeout() != null ? coreClientData.getTimeout() : 3600);
		saLoginModel.setExtra(LoginHelper.CLIENT_KEY, coreClientData.getClientId());
		saLoginModel.setExtra(LoginHelper.USER_KEY, loginUser.getUserId());
		// generate token
		LoginHelper.login(loginUser, saLoginModel);
		
		LoginResult loginResult = new LoginResult();
		loginResult.setAccessToken(StpUtil.getTokenValue());
		loginResult.setExpireIn(StpUtil.getTokenTimeout());
		loginResult.setClientId(coreClientData.getClientId());
		return loginResult;
	}
	
	@Override
	public void register(String body, CoreClientData coreClientData) {
		RegisterBody registerBody = JsonUtils.parseObject(body, RegisterBody.class);
		
		assert registerBody != null;
		String userName = registerBody.getUserName();
		String password = registerBody.getPassword();
		
		String userType = UserType.getUserType(registerBody.getUserType()).getUserType();
		
		String code = registerBody.getCode();
		String uuid = registerBody.getUuid();
		log.info("userName register: {}", userName);
		log.info("password register: {}", password);
		boolean captchaEnabled = captchaProperties.getEnable();
		if (captchaEnabled) {
			validateCaptcha(userName, code, uuid);
		}
		CoreUser coreUser = new CoreUser();
		coreUser.setUserName(userName);
		coreUser.setNickName(userName);
		coreUser.setEmail(userName);
		coreUser.setPassword(BCrypt.hashpw(password, "DnictPro@123"));
		coreUser.setUserType(userType);
		coreUser.setIsEnabled(true);
		
		boolean exist = coreUserService.existsByEmailIgnoreCaseAndDaXoaFalse(userName);
		if (exist) {
			throw new UserException("user.register.save.error", userName);
		}
		try {
			coreUserService.save(coreUser);
		} catch (Exception e) {
			throw new UserException("user.register.error");
		}
		
		coreUserService.recordLoginInfo(userName, Constants.REGISTER, MessageUtils.message("user.register.success"));
	}
	
	private void validateCaptcha(String userName, String code, String uuid) {
		String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + StringUtils.blankToDefault(uuid, "");
		String captcha = RedisUtils.getCacheObject(verifyKey);
		RedisUtils.deleteObject(verifyKey);
		if (captcha == null) {
			coreUserService.recordLoginInfo(userName, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
			throw new CaptchaExpireException();
		}
		if (!code.equalsIgnoreCase(captcha)) {
			coreUserService.recordLoginInfo(userName, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error"));
			throw new CaptchaException();
		}
	}
	
	private CoreUser loadUserByUsername(String userName) {
		Optional<CoreUser> optionalCoreUser = coreUserService.findFirstByEmailAndDaXoaFalse(userName);
		if (optionalCoreUser.isEmpty()) {
			log.info("Login user: {} does not exist.", userName);
			throw new UserException("user.not.exists", userName);
		} else if (!Boolean.TRUE.equals(optionalCoreUser.get().getIsEnabled())) {
			log.info("Logged in user: {} has been deactivated.", userName);
			throw new UserException("user.blocked", userName);
		}
		return optionalCoreUser.get();
	}
	
}
