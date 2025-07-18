package vn.tr.core.security.service;

import vn.tr.common.core.domain.model.LoginBody;
import vn.tr.common.core.domain.model.RegisterBody;
import vn.tr.common.core.exception.ServiceException;
import vn.tr.common.core.utils.SpringUtils;
import vn.tr.core.data.CoreClientData;
import vn.tr.core.data.LoginResult;

public interface IAuthStrategy {
	
	String BASE_NAME = "AuthStrategy";
	
	static LoginResult login(LoginBody loginBody, CoreClientData coreClientData, String grantType) {
		String beanName = grantType + BASE_NAME;
		if (!SpringUtils.containsBean(beanName)) {
			throw new ServiceException("Incorrect authorization type!");
		}
		IAuthStrategy instance = SpringUtils.getBean(beanName);
		return instance.login(loginBody, coreClientData);
	}
	
	LoginResult login(LoginBody loginBody, CoreClientData coreClientData);
	
	static void register(RegisterBody registerBody, CoreClientData coreClientData, String grantType) {
		String beanName = grantType + BASE_NAME;
		if (!SpringUtils.containsBean(beanName)) {
			throw new ServiceException("Incorrect authorization type!");
		}
		IAuthStrategy instance = SpringUtils.getBean(beanName);
		instance.register(registerBody, coreClientData);
	}
	
	void register(RegisterBody registerBody, CoreClientData coreClientData);
	
}
