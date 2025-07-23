package vn.tr.core.dao.service;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.constant.CacheConstants;
import vn.tr.common.core.constant.Constants;
import vn.tr.common.core.domain.model.LoginUser;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.common.core.enums.LoginType;
import vn.tr.common.core.exception.user.UserException;
import vn.tr.common.core.utils.MessageUtils;
import vn.tr.common.core.utils.ServletUtils;
import vn.tr.common.core.utils.SpringUtils;
import vn.tr.common.log.event.LoginInfoEvent;
import vn.tr.common.redis.utils.RedisUtils;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.dao.model.CoreUserApp;
import vn.tr.core.data.criteria.CoreUserSearchCriteria;

import java.time.Duration;
import java.util.*;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreUserServiceImpl implements CoreUserService {
	
	private final CoreUserRepo coreUserRepo;
	private final CorePermissionService corePermissionService;
	private final CoreUserRoleService coreUserRoleService;
	private final CoreUserGroupService coreUserGroupService;
	@Value("${user.password.maxRetryCount}")
	private Integer maxRetryCount;
	@Value("${user.password.lockTime}")
	private Integer lockTime;
	
	@Override
	public void deleteById(Long id) {
		coreUserRepo.deleteById(id);
	}
	
	@Override
	@Cacheable(value = "coreUser", key = "#id")
	public Optional<CoreUser> findById(Long id) {
		return coreUserRepo.findById(id);
	}
	
	@Override
	public CoreUser save(CoreUser coreUser) {
		return coreUserRepo.save(coreUser);
	}
	
	@Override
	public void delete(Long id) {
		coreUserRepo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return coreUserRepo.existsById(id);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Set<Long> ids) {
		if (ids.isEmpty()) {
			return;
		}
		coreUserRepo.softDeleteByIds(ids);
	}
	
	@Override
	public Page<CoreUser> findAll(CoreUserSearchCriteria coreUserSearchCriteria, Pageable pageable) {
		return coreUserRepo.findAll(CoreUserSpecifications.quickSearch(coreUserSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreUser> findAll(CoreUserSearchCriteria coreUserSearchCriteria) {
		return coreUserRepo.findAll(CoreUserSpecifications.quickSearch(coreUserSearchCriteria));
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean existsByUsernameIgnoreCase(String username) {
		return coreUserRepo.existsByUsernameIgnoreCase(username);
	}
	
	@Override
	public boolean existsByEmailIgnoreCase(String email) {
		return coreUserRepo.existsByEmailIgnoreCase(email);
	}
	
	@Override
	public boolean existsByIdNotAndUsernameIgnoreCase(long id, String username) {
		return coreUserRepo.existsByIdNotAndUsernameIgnoreCase(id, username);
	}
	
	@Override
	@Cacheable(value = "coreUser", key = "'username:' + #username.toLowerCase()")
	public Optional<CoreUser> findFirstByUsernameIgnoreCase(String username) {
		return coreUserRepo.findFirstByUsernameIgnoreCase(username);
	}
	
	@Override
	@Cacheable(value = "coreUser", key = "'email:' + #email.toLowerCase()")
	public Optional<CoreUser> findFirstByEmailIgnoreCase(String email) {
		return coreUserRepo.findFirstByEmailIgnoreCase(email);
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
	
	/**
	 * Build a LoginUser object from CoreUser and CoreUserApp.
	 *
	 * @param coreUser
	 * 		the user entity
	 * @param userAppAccess
	 * 		the user's app access entity
	 *
	 * @return LoginUser object with permissions, groups, and roles
	 */
	@Override
	public LoginUser buildLoginUser(CoreUser coreUser, CoreUserApp userAppAccess) {
		LoginUser loginUser = new LoginUser();
		loginUser.setUserId(coreUser.getId());
		loginUser.setUsername(coreUser.getUsername());
		loginUser.setFullName(coreUser.getFullName());
		loginUser.setUserType(userAppAccess.getUserTypeCode());
		loginUser.setPermissionCodes(corePermissionService.findAllCodesByUsernameAndAppCode(coreUser.getUsername(), userAppAccess.getAppCode()));
		loginUser.setGroupCodes(coreUserGroupService.findActiveGroupCodesByUsernameAndAppCode(coreUser.getUsername(), userAppAccess.getAppCode()));
		loginUser.setRoleCodes(coreUserRoleService.findActiveRoleCodesByUsernameAndAppCode(coreUser.getUsername(), userAppAccess.getAppCode()));
		return loginUser;
	}
	
	@Override
	public void checkUserAppStatus(CoreUserApp userAppAccess) {
		if (userAppAccess.getStatus() == null || userAppAccess.getStatus() == LifecycleStatus.INACTIVE) {
			log.info("Người dùng '{}' đăng nhập vào app '{}' đã bị vô hiệu hóa.", userAppAccess.getUsername(), userAppAccess.getAppCode());
			throw new UserException("user.blocked", userAppAccess.getUsername());
		}
		if (userAppAccess.getStatus() == LifecycleStatus.LOCKED) {
			log.info("Người dùng '{}' đăng nhập vào app '{}' đã bị khóa.", userAppAccess.getUsername(), userAppAccess.getAppCode());
			throw new UserException("user.password.retry.limit.exceed", userAppAccess.getUsername());
		}
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
	
	@Override
	@Transactional(readOnly = true)
	public List<CoreUser> findAllByIdIn(Collection<Long> ids) {
		if (ids.isEmpty()) {
			return Collections.emptyList();
		}
		return coreUserRepo.findAllById(ids);
	}
	
	@Override
	public void deleteByIds(Collection<Long> ids) {
	
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<CoreUser> findByUsernameIgnoreCase(String username) {
		return coreUserRepo.findFirstByUsernameIgnoreCase(username);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<CoreUser> findByUsernameIgnoreCaseIncludingDeleted(String username) {
		return coreUserRepo.findByUsernameIgnoreCaseIncludingDeleted(username);
	}
	
	@Override
	public JpaRepository<CoreUser, Long> getRepository() {
		return this.coreUserRepo;
	}

//	@Override
//	@Transactional
//	public CoreUser findOrCreate(String username, String fullName, String email, String rawPassword) {
//		// Chuẩn hóa username
//		String normalizedUsername = username.toLowerCase();
//
//		return coreUserRepo.findFirstByUsernameIgnoreCase(normalizedUsername)
//				.orElseGet(() -> {
//					CoreUser newUser = new CoreUser();
//					newUser.setUsername(normalizedUsername);
//					newUser.setFullName(fullName);
//					newUser.setEmail(email);
//					newUser.setHashedPassword(BCrypt.hashpw(rawPassword));
//					// Gán các giá trị mặc định khác nếu cần
//					// Ví dụ: newUser.setUserType(...)
//					return coreUserRepo.save(newUser);
//				});
//	}

}
