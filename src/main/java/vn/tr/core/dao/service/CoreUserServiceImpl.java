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
	private final CoreUserAppService coreUserAppService;
	private final CoreRolePermissionService coreRolePermissionService;
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
	//@Cacheable(value = "coreUser", key = "'username:' + #username.toLowerCase()")
	public Optional<CoreUser> findFirstByUsernameIgnoreCase(String username) {
		return coreUserRepo.findByUsernameIgnoreCaseIncludingDeleted(username);
	}
	
	@Override
	@Cacheable(value = "coreUser", key = "'email:' + #email.toLowerCase()")
	public Optional<CoreUser> findFirstByEmailIgnoreCase(String email) {
		return coreUserRepo.findFirstByEmailIgnoreCase(email);
	}
	
	@Override
	public void recordLoginInfo(String userName, String status, String message) {
		LoginInfoEvent loginInfoEvent = new LoginInfoEvent();
		loginInfoEvent.setUsername(userName);
		loginInfoEvent.setStatus(status);
		loginInfoEvent.setMessage(message);
		loginInfoEvent.setRequest(ServletUtils.getRequest());
		SpringUtils.context().publishEvent(loginInfoEvent);
	}
	
	// Trong lớp service chịu trách nhiệm đăng nhập
	@Override
	public LoginUser buildLoginUser(CoreUser coreUser) {
		LoginUser loginUser = new LoginUser();
		loginUser.setUserId(String.valueOf(coreUser.getId()));
		loginUser.setUsername(coreUser.getUsername());
		loginUser.setFullName(coreUser.getFullName());
		
		// Lấy và lưu trữ appCodes vì nó cần thiết cho các lần tải sau
		Set<String> appCodes = coreUserAppService.findActiveAppCodesByUsername(coreUser.getUsername());
		loginUser.setAppCodes(appCodes);
		Set<String> roleCodes = coreUserRoleService.findAllActiveRoleCodesByUsername(coreUser.getUsername());
		loginUser.setRoleCodes(roleCodes);
		// KHÔNG TẢI TRƯỚC QUYỀN HẠN, VAI TRÒ, NHÓM
		// loginUser.setPermissionCodes(...);
		// loginUser.setGroupCodes(...);
		// loginUser.setRoleCodes(...);
		
		// Cờ `permissionsLoaded` mặc định là `false`, không cần set.
		
		log.info("Built lightweight LoginUser (permissions will be loaded on demand): {}", loginUser);
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
	public LoginUser buildLoginUserForSingleApp(CoreUser user, String appCode) {
		CoreUserApp userAppAccess = coreUserAppService.findByUsernameAndAppCode(user.getUsername(), appCode)
				.orElseThrow(() -> new UserException("user.app.access.denied", appCode));
		checkUserAppStatus(userAppAccess);
		
		Set<String> roles = coreUserRoleService.findActiveRoleCodesByUsernameAndAppCode(user.getUsername(), appCode);
		Set<String> groups = coreUserGroupService.findActiveGroupCodesByUsernameAndAppCode(user.getUsername(), appCode);
		Set<String> permissions = roles.isEmpty()
				? Collections.emptySet()
				: coreRolePermissionService.findPermissionCodesByRoleCodesAndAppCode(roles, appCode);
		
		return createLoginUserObject(user, appCode, roles, groups, permissions);
	}
	
	private LoginUser createLoginUserObject(CoreUser user, String appCode, Set<String> roles,
			Set<String> groups, Set<String> permissions) {
		LoginUser loginUser = new LoginUser();
		loginUser.setUserId(String.valueOf(user.getId()));
		loginUser.setUsername(user.getUsername());
		loginUser.setFullName(user.getFullName());
		loginUser.setAppCodes(Set.of(appCode));
		loginUser.setRoleCodes(roles);
		loginUser.setGroupCodes(groups);
		loginUser.setPermissionCodes(permissions);
		return loginUser;
	}
	
	@Override
	public LoginUser buildAggregatedLoginUser(CoreUser user) {
		List<CoreUserApp> accessibleApps = coreUserAppService.findActiveByUsername(user.getUsername());
		if (accessibleApps.isEmpty()) {
			throw new UserException("user.no.app.assigned");
		}
		
		Set<String> allRoles = coreUserRoleService.findAllActiveRoleCodesByUsername(user.getUsername());
		Set<String> allGroups = coreUserGroupService.findAllActiveGroupCodesByUsername(user.getUsername());
		Set<String> allPermissions = allRoles.isEmpty()
				? Collections.emptySet()
				: coreRolePermissionService.findPermissionCodesByRoleCodes(allRoles);
		
		return createLoginUserObject(user, null, allRoles, allGroups, allPermissions);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<CoreUser> findByUsernameOrEmail(String identifier) {
		if (identifier.isBlank()) {
			return Optional.empty();
		}
		return coreUserRepo.findByUsernameOrEmailIgnoreCase(identifier);
	}
	
	@Override
	public JpaRepository<CoreUser, Long> getRepository() {
		return this.coreUserRepo;
	}
	
}
