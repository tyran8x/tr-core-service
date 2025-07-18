package vn.tr.core.security.service.strategy;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.tr.common.core.constant.Constants;
import vn.tr.common.core.domain.model.LoginBody;
import vn.tr.common.core.domain.model.LoginUser;
import vn.tr.common.core.domain.model.PasswordLoginBody;
import vn.tr.common.core.domain.model.RegisterBody;
import vn.tr.common.core.enums.LoginType;
import vn.tr.common.core.enums.UserType;
import vn.tr.common.core.exception.ServiceException;
import vn.tr.common.core.exception.user.UserException;
import vn.tr.common.core.utils.MessageUtils;
import vn.tr.common.core.utils.ValidatorUtils;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.dao.service.CoreRoleService;
import vn.tr.core.dao.service.CoreUserRoleService;
import vn.tr.core.dao.service.CoreUserService;
import vn.tr.core.data.CoreClientData;
import vn.tr.core.data.LoginResult;
import vn.tr.core.security.service.IAuthStrategy;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service("password" + IAuthStrategy.BASE_NAME)
@RequiredArgsConstructor
public class PasswordAuthStrategy implements IAuthStrategy {
	
	private final CoreUserService coreUserService;
	private final CoreRoleService coreRoleService;
	private final CoreUserRoleService coreUserRoleService;
	
	@Override
	public LoginResult login(LoginBody loginBody, CoreClientData coreClientData) {
		if (!(loginBody instanceof PasswordLoginBody passwordBody)) {
			throw new ServiceException("Dữ liệu không hợp lệ cho grant type password.");
		}
		ValidatorUtils.validate(passwordBody);
		
		String username = passwordBody.getUsername();
		String password = passwordBody.getPassword();
		String appCode = LoginHelper.getAppCode();
		
		log.info("username: {}", username);
		log.info("password: {}", password);
		
		CoreUser coreUser = loadUserByUsername(username);
		coreUserService.checkLogin(LoginType.PASSWORD, username, () -> !BCrypt.checkpw(password, coreUser.getHashedPassword()));
		LoginUser loginUser = coreUserService.buildLoginUser(coreUser, appCode);
		
		log.info("loginUser: {}", loginUser);
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
		String username = registerBody.getUsername();
		String password = registerBody.getPassword();
		
		String userType = UserType.getUserType(registerBody.getUserType()).getUserType();
		
		log.info("username register: {}", username);
		log.info("password register: {}", password);
		
		CoreUser coreUser = new CoreUser();
//		coreUser.setUserName(username);
//		coreUser.setNickName(username);
//		coreUser.setEmail(username);
//		coreUser.setPassword(BCrypt.hashpw(password));
//		coreUser.setUserType(userType);
//		coreUser.setIsEnabled(true);
//
		boolean exist = coreUserService.existsByUsernameIgnoreCase(username);
		if (exist) {
			throw new UserException("user.register.save.error", username);
		}
		try {
			coreUserService.save(coreUser);
			
			Set<String> roles = registerBody.getRoles();
			//		List<CoreRole> coreRoles = coreRoleService.findByMaIgnoreCaseInAndDaXoaFalse(roles);

//			coreUserRoleService.setFixedDaXoaForUserName(true, coreUser.getUsername());
//			if (CollUtil.isNotEmpty(coreRoles)) {
//				for (CoreRole coreRole : coreRoles) {
//					CoreUserRole coreUserRole = new CoreUserRole();
//					Optional<CoreUserRole> optionalCoreUserRole = coreUserRoleService.findFirstByRoleAndUserName(coreRole.getCode(),
//							coreUser.getUsername());
//					if (optionalCoreUserRole.isPresent()) {
//						coreUserRole = optionalCoreUserRole.get();
//					}
//					coreUserRole.setDaXoa(false);
//					coreUserRole.setRoleCode(coreRole.getCode());
//					coreUserRole.setUsername(coreUser.getUsername());
//					coreUserRoleService.save(coreUserRole);
//				}
//			}
		} catch (Exception e) {
			throw new UserException("user.register.error");
		}
		
		coreUserService.recordLoginInfo(username, Constants.REGISTER, MessageUtils.message("user.register.success"));
	}
	
}
