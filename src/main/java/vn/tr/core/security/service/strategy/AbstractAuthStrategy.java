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
import vn.tr.common.core.constant.SecurityConstants;
import vn.tr.common.core.domain.model.LoginUser;
import vn.tr.common.core.domain.model.RegisterBody;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.common.core.exception.user.UserException;
import vn.tr.common.core.utils.MessageUtils;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.dao.model.CoreUserApp;
import vn.tr.core.dao.service.CoreUserAppService;
import vn.tr.core.dao.service.CoreUserRoleService;
import vn.tr.core.dao.service.CoreUserService;
import vn.tr.core.data.LoginResult;
import vn.tr.core.security.service.IAuthStrategy;

import java.util.Collections;
import java.util.Set;

/**
 * Lớp cha trừu tượng cho TẤT CẢ các chiến lược xác thực. Chứa logic chung và đã được tối ưu cho: - Phân luồng đăng nhập (Super Admin vs. Người dùng
 * thường). - Xây dựng đối tượng LoginUser (cho 1 app hoặc tổng hợp). - Tương tác với Sa-Token để tạo token. - Xử lý nghiệp vụ đăng ký người dùng.
 *
 * @author tyran8x
 * @version 3.0
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractAuthStrategy implements IAuthStrategy {
	
	protected final CoreUserService coreUserService;
	protected final CoreUserAppService coreUserAppService;
	protected final CoreUserRoleService coreUserRoleService;
	
	/**
	 * Phương thức điều phối luồng đăng nhập chính. Nó nhận vào người dùng đã được xác thực và quyết định luồng xử lý tiếp theo.
	 *
	 * @param user          Thực thể CoreUser đã được xác thực thành công.
	 * @param targetAppCode Mã ứng dụng người dùng muốn đăng nhập vào (có thể là null).
	 *
	 * @return Kết quả đăng nhập chứa token.
	 */
	protected LoginResult processLogin(CoreUser user, String targetAppCode) {
		if (coreUserRoleService.isSuperAdmin(user.getUsername())) {
			return performSuperAdminLogin(user);
		}
		
		// Người dùng thường đi theo luồng chuẩn
		return performStandardUserLogin(user, targetAppCode);
	}
	
	private LoginResult performSuperAdminLogin(CoreUser user) {
		log.info("Performing optimized login for Super Admin: {}", user.getUsername());
		
		LoginUser loginUser = buildSuperAdminLoginUser(user);
		
		LoginHelper.login(loginUser, createLoginParameter(loginUser));
		coreUserService.recordLoginInfo(user.getUsername(), Constants.LOGIN, "Super Admin login successful.");
		return createLoginResult();
	}
	
	private LoginResult performStandardUserLogin(CoreUser user, String targetAppCode) {
		LoginUser loginUser;
		if (StrUtil.isNotBlank(targetAppCode)) {
			log.info("Performing login for user '{}' into specific app: {}", user.getUsername(), targetAppCode);
			loginUser = coreUserService.buildLoginUserForSingleApp(user, targetAppCode);
		} else {
			log.info("Performing aggregated login for user '{}' across all apps.", user.getUsername());
			loginUser = coreUserService.buildAggregatedLoginUser(user);
		}
		
		LoginHelper.login(loginUser, createLoginParameter(loginUser));
		coreUserService.recordLoginInfo(user.getUsername(), Constants.LOGIN, MessageUtils.message("user.login.success"));
		return createLoginResult();
	}
	
	private LoginUser buildSuperAdminLoginUser(CoreUser user) {
		LoginUser loginUser = new LoginUser();
		loginUser.setUserId(String.valueOf(user.getId()));
		loginUser.setUsername(user.getUsername());
		loginUser.setFullName(user.getFullName());
		loginUser.setAppCodes(Set.of(SecurityConstants.SYSTEM_APP_CODE));
		loginUser.setRoleCodes(Set.of(SecurityConstants.ROLE_SUPER_ADMIN));
		loginUser.setPermissionCodes(Set.of("*"));
		loginUser.setGroupCodes(Collections.emptySet());
		return loginUser;
	}
	
	private SaLoginParameter createLoginParameter(LoginUser loginUser) {
		return new SaLoginParameter()
//				.setDeviceType(client.getDeviceType())
//				.setTimeout(client.getTimeout())
//				.setActiveTimeout(client.getActiveTimeout())
				.setExtra(SecurityConstants.USER_ID, loginUser.getUserId())
				.setExtra(SecurityConstants.USERNAME, loginUser.getUsername())
				.setExtra(SecurityConstants.APP_CODE, loginUser.getAppCodes());
	}
	
	private LoginResult createLoginResult() {
		return LoginResult.builder()
				.accessToken(StpUtil.getTokenValue())
				.expireIn(StpUtil.getTokenTimeout())
				.tokenType(SecurityConstants.TOKEN_PREFIX.trim())
				.build();
	}
	
	/**
	 * Xử lý nghiệp vụ đăng ký người dùng mới. Lớp con có thể gọi hook `preRegisterValidate` để kiểm tra captcha hoặc các điều kiện khác.
	 */
	@Override
	@Transactional
	public void performRegistration(RegisterBody registerBody) {
		preRegisterValidate(registerBody);
		
		String username = registerBody.getUsername();
		String password = registerBody.getPassword();
		String appCode = registerBody.getAppCode();
		
		if (StrUtil.isBlank(appCode)) {
			throw new UserException("register.appcode.required");
		}
		if (coreUserService.existsByUsernameIgnoreCase(username)) {
			throw new UserException("user.register.username.exists", username);
		}
		if (StrUtil.isNotBlank(registerBody.getEmail()) && coreUserService.existsByEmailIgnoreCase(registerBody.getEmail())) {
			throw new UserException("user.register.email.exists", registerBody.getEmail());
		}
		
		CoreUser coreUser = new CoreUser();
		coreUser.setUsername(username);
		coreUser.setHashedPassword(BCrypt.hashpw(password));
		coreUser.setEmail(registerBody.getEmail());
		coreUser.setFullName(registerBody.getFullName());
		coreUser.setStatus(LifecycleStatus.ACTIVE);
		coreUserService.save(coreUser);
		
		CoreUserApp firstAccess = new CoreUserApp();
		firstAccess.setUsername(username);
		firstAccess.setAppCode(appCode);
		firstAccess.setUserTypeCode(StrUtil.emptyToDefault(registerBody.getUserTypeCode(), "EXTERNAL"));
		firstAccess.setStatus(LifecycleStatus.ACTIVE);
		coreUserAppService.save(firstAccess);
		
		coreUserService.recordLoginInfo(username, Constants.REGISTER, MessageUtils.message("user.register.success"));
	}
	
	/**
	 * Hook method cho các lớp con chèn logic validation trước khi đăng ký.
	 */
	protected void preRegisterValidate(RegisterBody registerBody) {
		// Mặc định không làm gì
	}
	
}
