package vn.tr.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.common.core.domain.model.LoginBody;
import vn.tr.common.core.domain.model.LoginUser;
import vn.tr.common.core.domain.model.RegisterBody;
import vn.tr.common.core.exception.ServiceException;
import vn.tr.common.core.utils.StringUtils;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.common.web.annotation.AppCode;
import vn.tr.core.business.CoreClientBusiness;
import vn.tr.core.dao.service.CoreUserService;
import vn.tr.core.data.LoginResult;
import vn.tr.core.data.dto.CoreClientData;
import vn.tr.core.security.service.IAuthStrategy;

/**
 * Controller trung tâm cho các nghiệp vụ Xác thực (Authentication).
 * Đóng vai trò là Token Endpoint, xử lý các luồng đăng nhập, đăng ký và đăng xuất.
 *
 * @author tyran8x
 * @version 3.0
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
	
	private final CoreClientBusiness coreClientBusiness;
	private final CoreUserService coreUserService;
	
	/**
	 * Endpoint đăng nhập chính.
	 * Hỗ trợ nhiều loại hình đăng nhập (grant types) thông qua Strategy Pattern.
	 *
	 * @param loginBody DTO chứa thông tin đăng nhập, bao gồm grant_type, username/password, clientId/clientSecret.
	 * @param appCode   Ngữ cảnh ứng dụng, được tự động điền bởi @AppCode Argument Resolver.
	 *
	 * @return Kết quả đăng nhập chứa access token và thông tin người dùng.
	 */
	@PostMapping("/login")
	public R<LoginResult> login(@Valid @RequestBody LoginBody loginBody, @AppCode String appCode) {
		String grantType = loginBody.getGrantType();
		if (StringUtils.isBlank(grantType)) {
			throw new ServiceException("grant_type là bắt buộc.");
		}
		
		// --- BƯỚC 1: Xác thực Client (Ứng dụng) ---
		// Mọi yêu cầu xác thực đều phải đến từ một client hợp lệ.
		//CoreClientData client = coreClientBusiness.validateClient(loginBody.getClientId(), loginBody.getClientSecret(), grantType);
		
		// --- BƯỚC 2: Ủy quyền cho Strategy Pattern xử lý nghiệp vụ ---
		// IAuthStrategy sẽ tìm bean phù hợp (vd: "passwordAuthStrategy") và thực thi.
		LoginResult loginResult = IAuthStrategy.executeLogin(loginBody, appCode);
		
		// --- BƯỚC 3: (Tùy chọn) Thực hiện các hành động sau khi đăng nhập thành công ---
		// Ví dụ: Ghi log, gửi thông báo...
		log.info("User '{}' logged in successfully via grant_type '{}'.", LoginHelper.getUsername(), grantType);
		
		return R.ok(loginResult);
	}
	
	/**
	 * Endpoint đăng ký người dùng mới.
	 *
	 * @param registerBody DTO chứa thông tin đăng ký.
	 *
	 * @return R.ok() nếu đăng ký thành công.
	 */
	@PostMapping("/register")
	public R<Void> register(@Valid @RequestBody RegisterBody registerBody) {
		// Tương tự login, việc đăng ký cũng phải được thực hiện bởi một client hợp lệ.
		CoreClientData client = coreClientBusiness.validateClient(
				registerBody.getClientId(),
				registerBody.getClientSecret(),
				"register" // Sử dụng một grant type đặc biệt cho đăng ký
		                                                         );
		
		// Ủy quyền cho Strategy Pattern.
		IAuthStrategy.executeRegister(registerBody);
		
		return R.ok("Đăng ký thành công.");
	}
	
	/**
	 * Endpoint đăng xuất.
	 * Vô hiệu hóa token của người dùng hiện tại.
	 *
	 * @return R.ok()
	 */
	@DeleteMapping("/logout")
	public R<Void> logout() {
		coreUserService.logout();
		return R.ok("Đăng xuất thành công.");
	}
	
	/**
	 * Endpoint lấy thông tin chi tiết của người dùng đang đăng nhập.
	 *
	 * @return Đối tượng LoginUser chứa thông tin và quyền hạn.
	 */
	@GetMapping("/info")
	public R<LoginUser> getInfo() {
		// LoginHelper sẽ ném exception nếu người dùng chưa đăng nhập.
		LoginUser loginUser = LoginHelper.getLoginUserOrThrow();
		return R.ok(loginUser);
	}
}
