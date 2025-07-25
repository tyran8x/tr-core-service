package vn.tr.core.security.service.strategy;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.constant.Constants;
import vn.tr.common.core.constant.GlobalConstants;
import vn.tr.common.core.domain.model.EmailLoginBody;
import vn.tr.common.core.domain.model.LoginBody;
import vn.tr.common.core.domain.model.LoginUser;
import vn.tr.common.core.domain.model.RegisterBody;
import vn.tr.common.core.enums.LoginType;
import vn.tr.common.core.exception.ServiceException;
import vn.tr.common.core.exception.user.UserException;
import vn.tr.common.core.utils.MessageUtils;
import vn.tr.common.redis.utils.RedisUtils;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.dao.model.CoreUserApp;
import vn.tr.core.dao.service.CoreUserAppService;
import vn.tr.core.dao.service.CoreUserService;
import vn.tr.core.data.LoginResult;
import vn.tr.core.data.dto.CoreClientData;
import vn.tr.core.security.service.IAuthStrategy;

import java.util.Optional;

@Slf4j
@Service("email" + IAuthStrategy.BASE_NAME)
@RequiredArgsConstructor
public class EmailAuthStrategy implements IAuthStrategy {
	
	private final CoreUserService coreUserService;
	private final CoreUserAppService coreUserAppService;
	
	@Override
	public LoginResult performLogin(LoginBody loginBody, CoreClientData coreClientData, String appCode) {
		if (!(loginBody instanceof EmailLoginBody emailLoginBody)) {
			throw new ServiceException("Dữ liệu không hợp lệ cho grant type email.");
		}
		
		String email = emailLoginBody.getEmail();
		String otpCode = emailLoginBody.getCode();
		
		if (appCode == null || appCode.isBlank()) {
			appCode = emailLoginBody.getAppCode();
		}
		
		// 1. Xác thực mã OTP
		validateOtp(email, otpCode);
		
		// 2. Tìm người dùng bằng email
		CoreUser coreUser = coreUserService.findFirstByEmailIgnoreCase(email)
				.orElseThrow(() -> new UserException("user.not.exists", email));
		
		// 3. Kiểm tra quyền truy cập ứng dụng
		CoreUserApp userAppAccess = coreUserAppService.findByUsernameAndAppCode(coreUser.getUsername(), appCode)
				.orElseThrow(() -> new UserException("user.app.access.denied"));
		
		coreUserService.checkUserAppStatus(userAppAccess);
		
		// 4. Ghi log đăng nhập thành công
		coreUserService.checkLogin(LoginType.EMAIL, coreUser.getUsername(), () -> false);
		
		// 5. Xây dựng LoginUser và tạo token (logic này giống hệt các strategy khác)
		LoginUser loginUser = coreUserService.buildLoginUser(coreUser, userAppAccess);
		loginUser.setClientKey(coreClientData.getClientKey());
		loginUser.setDeviceType(coreClientData.getDeviceType());
		
		SaLoginParameter saLoginParameter = new SaLoginParameter();
		saLoginParameter.setDeviceType(coreClientData.getDeviceType());
		saLoginParameter.setTimeout(coreClientData.getTimeout() != null ? coreClientData.getTimeout() : 604800);
		saLoginParameter.setActiveTimeout(coreClientData.getActiveTimeout() != null ? coreClientData.getTimeout() : 3600);
		saLoginParameter.setExtra(LoginHelper.CLIENT_KEY, coreClientData.getClientId());
		saLoginParameter.setExtra(LoginHelper.USER_KEY, loginUser.getUserId());
		saLoginParameter.setExtra(LoginHelper.APP_CODE, appCode);
		
		LoginHelper.login(loginUser, saLoginParameter);
		
		LoginResult loginResult = new LoginResult();
		loginResult.setAccessToken(StpUtil.getTokenValue());
		loginResult.setExpireIn(StpUtil.getTokenTimeout());
		loginResult.setClientId(coreClientData.getClientId());
		loginResult.setTokenType(StpUtil.getTokenName());
		return loginResult;
	}
	
	@Override
	@Transactional
	public void performRegistration(RegisterBody registerBody, CoreClientData coreClientData) {
		String username = registerBody.getUsername();
		String password = registerBody.getPassword();
		String appCode = registerBody.getAppCode();
		
		if (appCode == null || appCode.isBlank()) {
			throw new ServiceException("App code là bắt buộc khi đăng ký.");
		}
		
		// Kiểm tra trùng lặp
		if (coreUserService.existsByUsernameIgnoreCase(username)) {
			throw new UserException("user.register.username.exists", username);
		}
		if (coreUserService.existsByEmailIgnoreCase(username)) {
			throw new UserException("user.register.email.exists", username);
		}
		
		try {
			// Tạo người dùng mới
			CoreUser coreUser = new CoreUser();
			coreUser.setUsername(username);
			// Email là bắt buộc cho luồng này
			coreUser.setEmail(username);
			// Vẫn lưu hash password để người dùng có thể đăng nhập bằng cả 2 cách
			coreUser.setHashedPassword(BCrypt.hashpw(password));
			coreUserService.save(coreUser);
			
			// Gán quyền truy cập ứng dụng
			CoreUserApp firstAccess = new CoreUserApp();
			firstAccess.setUsername(username);
			firstAccess.setAppCode(appCode);
			firstAccess.setUserTypeCode(Optional.ofNullable(registerBody.getUserTypeCode()).orElse("EXTERNAL"));
			coreUserAppService.save(firstAccess);
			
			// Có thể gán vai trò mặc định ở đây nếu cần
			// ...
			
		} catch (Exception e) {
			log.error("Lỗi khi đăng ký người dùng '{}': {}", username, e.getMessage(), e);
			throw new UserException("user.register.error");
		}
		
		coreUserService.recordLoginInfo(username, Constants.REGISTER, MessageUtils.message("user.register.success"));
	}
	
	/**
	 * Helper method để xác thực mã OTP.
	 *
	 * @param email   Email của người dùng.
	 * @param otpCode Mã OTP người dùng nhập.
	 */
	private void validateOtp(String email, String otpCode) {
		if (otpCode == null || otpCode.isBlank()) {
			throw new UserException("Vui lòng nhập mã xác thực.");
		}
		String verifyKey = GlobalConstants.EMAIL_CODE_KEY + email;
		String correctOtp = RedisUtils.getCacheObject(verifyKey);
		
		if (correctOtp == null) {
			throw new UserException("Mã xác thực đã hết hạn hoặc không tồn tại.");
		}
		
		if (!otpCode.equalsIgnoreCase(correctOtp)) {
			// Có thể thêm cơ chế đếm số lần nhập sai ở đây
			throw new UserException("Mã xác thực không chính xác.");
		}
		
		// Xóa OTP sau khi đã sử dụng thành công
		RedisUtils.deleteObject(verifyKey);
	}
}
