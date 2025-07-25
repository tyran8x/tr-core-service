package vn.tr.core.security.service;

import vn.tr.common.core.domain.model.LoginBody;
import vn.tr.common.core.domain.model.RegisterBody;
import vn.tr.common.core.exception.ServiceException;
import vn.tr.common.core.utils.SpringUtils;
import vn.tr.core.data.LoginResult;
import vn.tr.core.data.dto.CoreClientData;

/**
 * Interface định nghĩa "hợp đồng" cho các chiến lược xác thực khác nhau trong hệ thống.
 */
public interface IAuthStrategy {
	
	String BASE_NAME = "AuthStrategy";
	String DEFAULT_REGISTER_STRATEGY_BEAN_NAME = "password2captcha" + BASE_NAME;
	
	/**
	 * (SỬA) Điểm vào (Entry Point) để điều phối việc đăng nhập. Tên mới: executeLogin để phân biệt với phương thức instance. Nó tìm và ủy quyền cho
	 * chiến lược phù hợp.
	 *
	 * @param loginBody      Đối tượng chứa thông tin đăng nhập.
	 * @param coreClientData Dữ liệu của client.
	 *
	 * @return Kết quả đăng nhập chứa token.
	 */
	static LoginResult executeLogin(LoginBody loginBody, CoreClientData coreClientData, String appCode) {
		String grantType = loginBody.getGrantType();
		if (grantType == null || grantType.isBlank()) {
			throw new ServiceException("Grant type không được để trống.");
		}
		
		String beanName = grantType + BASE_NAME;
		if (!SpringUtils.containsBean(beanName)) {
			throw new ServiceException("Loại ủy quyền '" + grantType + "' không được hỗ trợ.");
		}
		IAuthStrategy instance = SpringUtils.getBean(beanName);
		// Gọi phương thức instance để thực thi logic
		return instance.performLogin(loginBody, coreClientData, appCode);
	}
	
	/**
	 * (SỬA) Phương thức instance để thực thi logic đăng nhập cụ thể. Tên mới: performLogin.
	 */
	LoginResult performLogin(LoginBody loginBody, CoreClientData coreClientData, String appCode);
	
	/**
	 * (SỬA) Điểm vào (Entry Point) để điều phối việc đăng ký. Tên mới: executeRegister. Nó tìm và ủy quyền cho chiến lược đăng ký mặc định.
	 *
	 * @param registerBody   Đối tượng chứa thông tin đăng ký.
	 * @param coreClientData Dữ liệu của client.
	 */
	static void executeRegister(RegisterBody registerBody, CoreClientData coreClientData) {
		if (!SpringUtils.containsBean(DEFAULT_REGISTER_STRATEGY_BEAN_NAME)) {
			throw new ServiceException("Không tìm thấy dịch vụ đăng ký mặc định.");
		}
		IAuthStrategy instance = SpringUtils.getBean(DEFAULT_REGISTER_STRATEGY_BEAN_NAME);
		// Gọi phương thức instance để thực thi logic
		instance.performRegistration(registerBody, coreClientData);
	}
	
	/**
	 * (SỬA) Phương thức instance để thực thi logic đăng ký cụ thể. Tên mới: performRegistration.
	 */
	default void performRegistration(RegisterBody registerBody, CoreClientData coreClientData) {
		throw new UnsupportedOperationException("Chức năng đăng ký không được hỗ trợ bởi chiến lược này.");
	}
}
