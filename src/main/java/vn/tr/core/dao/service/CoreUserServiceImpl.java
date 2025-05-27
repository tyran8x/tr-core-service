package vn.tr.core.dao.service;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.tr.common.core.constant.CacheConstants;
import vn.tr.common.core.constant.Constants;
import vn.tr.common.core.domain.model.LoginUser;
import vn.tr.common.core.enums.LoginType;
import vn.tr.common.core.exception.user.UserException;
import vn.tr.common.core.utils.MessageUtils;
import vn.tr.common.core.utils.ServletUtils;
import vn.tr.common.core.utils.SpringUtils;
import vn.tr.common.log.event.LoginInfoEvent;
import vn.tr.common.redis.utils.RedisUtils;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.dao.model.CoreUserConnect;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class CoreUserServiceImpl implements CoreUserService {
	
	private final CoreUserRepo repo;
	private final CorePermissionService corePermissionService;
	private final CoreUser2RoleService coreUser2RoleService;
	private final CoreUserConnectService coreUserConnectService;
	@Value("${user.password.maxRetryCount}")
	private Integer maxRetryCount;
	@Value("${user.password.lockTime}")
	private Integer lockTime;
	
	@Override
	public Optional<CoreUser> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public CoreUser save(CoreUser coreRole) {
		return repo.save(coreRole);
	}
	
	@Override
	public void delete(Long id) {
		repo.deleteById(id);
	}
	
	@Override
	public Page<CoreUser> findAll(String email, String name, List<String> roles, String appCode, Pageable pageable) {
		return repo.findAll(CoreUserSpecifications.quickSearch(email, name, roles, appCode), pageable);
	}
	
	@Override
	public boolean existsByEmailIgnoreCaseAndDaXoaFalse(String email) {
		return repo.existsByEmailIgnoreCaseAndDaXoaFalse(email);
	}
	
	@Override
	public boolean existsByIdNotAndEmailIgnoreCaseAndDaXoaFalse(long id, String email) {
		return repo.existsByIdNotAndEmailIgnoreCaseAndDaXoaFalse(id, email);
	}
	
	@Override
	public Optional<CoreUser> findFirstByEmailAndDaXoaFalse(String email) {
		return repo.findFirstByEmailAndDaXoaFalse(email);
	}
	
	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}
	
	@Override
	public void recordLoginInfo(String userName, String status, String message) {
		LoginInfoEvent loginInfoEvent = new LoginInfoEvent();
		loginInfoEvent.setUserName(userName);
		loginInfoEvent.setStatus(status);
		loginInfoEvent.setMessage(message);
		loginInfoEvent.setRequest(ServletUtils.getRequest());
		SpringUtils.context().publishEvent(loginInfoEvent);
	}
	
	@Override
	public LoginUser buildLoginUser(CoreUser coreUser) {
		LoginUser loginUser = new LoginUser();
		loginUser.setUserId(coreUser.getId());
		loginUser.setUserName(coreUser.getUserName());
		loginUser.setNickName(coreUser.getNickName());
		loginUser.setUserType(coreUser.getUserType());
		loginUser.setMenuPermission(corePermissionService.getMenuPermission(coreUser.getUserName()));
		loginUser.setRolePermission(corePermissionService.getRolePermission(coreUser.getUserName()));
		loginUser.setRoles(coreUser2RoleService.getRoleByUserName(coreUser.getUserName()));
		List<CoreUserConnect> coreUserConnects = coreUserConnectService.findByUserNameIgnoreCaseAndDaXoaFalse(coreUser.getUserName());
		Map<String, String> connects = new HashMap<>();
		if (CollUtil.isNotEmpty(coreUserConnects)) {
			for (CoreUserConnect coreUserConnect : coreUserConnects) {
				connects.put(coreUserConnect.getAppName(), coreUserConnect.getAppUserId());
			}
		}
		connects.putIfAbsent("mail", coreUser.getEmail());
		
		loginUser.setConnects(connects);
		return loginUser;
	}
	
	@Override
	public void checkLogin(LoginType loginType, String userName, Supplier<Boolean> supplier) {
		String errorKey = CacheConstants.PWD_ERR_CNT_KEY + userName;
		String loginFail = Constants.LOGIN_FAIL;
		
		int errorNumber = ObjectUtil.defaultIfNull(RedisUtils.getCacheObject(errorKey), 0);
		if (errorNumber >= maxRetryCount) {
			recordLoginInfo(userName, loginFail, MessageUtils.message(loginType.getRetryLimitExceed(), maxRetryCount, lockTime));
			throw new UserException(loginType.getRetryLimitExceed(), maxRetryCount, lockTime);
		}
		
		if (supplier.get()) {
			errorNumber++;
			RedisUtils.setCacheObject(errorKey, errorNumber, Duration.ofMinutes(lockTime));
			if (errorNumber >= maxRetryCount) {
				recordLoginInfo(userName, loginFail, MessageUtils.message(loginType.getRetryLimitExceed(), maxRetryCount, lockTime));
				throw new UserException(loginType.getRetryLimitExceed(), maxRetryCount, lockTime);
			} else {
				recordLoginInfo(userName, loginFail, MessageUtils.message(loginType.getRetryLimitCount(), errorNumber));
				throw new UserException(loginType.getRetryLimitCount(), errorNumber);
			}
		}
		RedisUtils.deleteObject(errorKey);
	}
	
	public void logout() {
		try {
			LoginUser loginUser = LoginHelper.getLoginUser();
			if (ObjectUtil.isNull(loginUser)) {
				return;
			}
			recordLoginInfo(loginUser.getUserName(), Constants.LOGOUT, MessageUtils.message("user.logout.success"));
		} catch (NotLoginException ignored) {
		} finally {
			try {
				StpUtil.logout();
			} catch (NotLoginException ignored) {
			}
		}
	}
	
}
