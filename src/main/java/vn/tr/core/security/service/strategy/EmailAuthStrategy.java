package vn.tr.core.security.service.strategy;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.tr.common.core.constant.Constants;
import vn.tr.common.core.domain.model.EmailLoginBody;
import vn.tr.common.core.domain.model.LoginUser;
import vn.tr.common.core.domain.model.RegisterBody;
import vn.tr.common.core.enums.LoginType;
import vn.tr.common.core.enums.UserType;
import vn.tr.common.core.exception.user.UserException;
import vn.tr.common.core.utils.MessageUtils;
import vn.tr.common.core.utils.ValidatorUtils;
import vn.tr.common.json.utils.JsonUtils;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.dao.service.CoreUserService;
import vn.tr.core.data.CoreClientData;
import vn.tr.core.data.LoginResult;
import vn.tr.core.security.service.IAuthStrategy;

import java.util.Optional;

@Slf4j
@Service("email" + IAuthStrategy.BASE_NAME)
@RequiredArgsConstructor
public class EmailAuthStrategy implements IAuthStrategy {
	
	private final CoreUserService coreUserService;
	
	@Override
	public LoginResult login(String body, CoreClientData coreClientData) {
		EmailLoginBody loginBody = JsonUtils.parseObject(body, EmailLoginBody.class);
		log.info("loginBody: {}", loginBody);
		ValidatorUtils.validate(loginBody);
		log.info("pass loginBody");
		assert loginBody != null;
		
		String userName = loginBody.getEmail();
		
		log.info("userName: {}", userName);
		
		CoreUser coreUser = loadUserByUsername(userName);
		coreUserService.checkLogin(LoginType.EMAIL, userName, () -> false);
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
		String userName = registerBody.getUsername();
		String password = registerBody.getPassword();
		
		String userType = UserType.getUserType(registerBody.getUserType()).getUserType();
		
		log.info("userName register: {}", userName);
		log.info("password register: {}", password);
		
		CoreUser coreUser = new CoreUser();
//		coreUser.setUserName(userName);
//		coreUser.setNickName(userName);
//		coreUser.setEmail(userName);
//		coreUser.setPassword(BCrypt.hashpw(password));
//		coreUser.setUserType(userType);
//		coreUser.setIsEnabled(true);
		
		boolean exist = coreUserService.existsByUsernameIgnoreCaseAndDaXoaFalse(userName);
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
	
	private CoreUser loadUserByUsername(String userName) {
		Optional<CoreUser> optionalCoreUser = coreUserService.findFirstByUsernameAndDaXoaFalse(userName);
		if (optionalCoreUser.isEmpty()) {
			log.info("Login user: {} does not exist.", userName);
			throw new UserException("user.not.exists", userName);
		}
//		else if (!Boolean.TRUE.equals(optionalCoreUser.get().getIsEnabled())) {
//			log.info("Logged in user: {} has been deactivated.", userName);
//			throw new UserException("user.blocked", userName);
//		}
		return optionalCoreUser.get();
	}
	
}
