package vn.tr.core.dao.service;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import vn.tr.core.data.criteria.CoreUserSearchCriteria;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class CoreUserServiceImpl implements CoreUserService {
	
	private final CoreUserRepo repo;
	private final CorePermissionService corePermissionService;
	private final CoreUserRoleService coreUserRoleService;
	@Value("${user.password.maxRetryCount}")
	private Integer maxRetryCount;
	@Value("${user.password.lockTime}")
	private Integer lockTime;
	
	@Override
	public void deleteById(Long id) {
		repo.deleteById(id);
	}
	
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
	public Page<CoreUser> findAll(CoreUserSearchCriteria coreUserSearchCriteria, Pageable pageable) {
		return repo.findAll(CoreUserSpecifications.quickSearch(coreUserSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreUser> findAll(CoreUserSearchCriteria coreUserSearchCriteria) {
		return repo.findAll(CoreUserSpecifications.quickSearch(coreUserSearchCriteria));
	}
	
	@Override
	public boolean existsByUsernameIgnoreCase(String username) {
		return repo.existsByUsernameIgnoreCase(username);
	}
	
	@Override
	public boolean existsByIdNotAndUsernameIgnoreCase(long id, String username) {
		return repo.existsByIdNotAndUsernameIgnoreCase(id, username);
	}
	
	@Override
	public Optional<CoreUser> findFirstByUsernameIgnoreCase(String username) {
		return repo.findFirstByUsernameIgnoreCase(username);
	}
	
	@Override
	public Optional<CoreUser> findFirstByEmailIgnoreCase(String email) {
		return repo.findFirstByEmailIgnoreCase(email);
	}
	
	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Set<Long> ids) {
		if (ids.isEmpty()) {
			return;
		}
		repo.softDeleteByIds(ids);
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
		loginUser.setUsername(coreUser.getUsername());
		loginUser.setFullName(coreUser.getFullName());
//		loginUser.setUserType(coreUser.getUserType());
//		loginUser.setMenuPermission(corePermissionService.getMenuPermission(coreUser.getUsername()));
//		loginUser.setRolePermission(corePermissionService.getRolePermission(coreUser.getUsername()));
//		loginUser.setRoles(coreUserRoleService.getRoleByUserName(coreUser.getUsername()));
//		List<CoreUserConnect> coreUserConnects = coreUserConnectService.findByUserNameIgnoreCase(coreUser.getUsername());
//		Map<String, String> connects = new HashMap<>();
//		if (CollUtil.isNotEmpty(coreUserConnects)) {
//			for (CoreUserConnect coreUserConnect : coreUserConnects) {
//				connects.put(coreUserConnect.getAppName(), coreUserConnect.getAppUserId());
//			}
//		}
		//	connects.putIfAbsent("mail", coreUser.getUsername());
		
		//	loginUser.setConnects(connects);
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
			recordLoginInfo(loginUser.getUsername(), Constants.LOGOUT, MessageUtils.message("user.logout.success"));
		} catch (NotLoginException ignored) {
		} finally {
			try {
				StpUtil.logout();
			} catch (NotLoginException ignored) {
			}
		}
	}
	
}
