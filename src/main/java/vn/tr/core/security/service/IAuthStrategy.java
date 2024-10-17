package vn.tr.core.security.service;

import vn.tr.common.core.exception.ServiceException;
import vn.tr.common.core.utils.SpringUtils;
import vn.tr.core.data.CoreClientData;
import vn.tr.core.data.LoginResult;

public interface IAuthStrategy {

	String BASE_NAME = "AuthStrategy";

	static LoginResult login(String body, CoreClientData coreClientData, String grantType) {
		String beanName = grantType + BASE_NAME;
		if (!SpringUtils.containsBean(beanName)) {
			throw new ServiceException("Incorrect authorization type!");
		}
		IAuthStrategy instance = SpringUtils.getBean(beanName);
		return instance.login(body, coreClientData);
	}

	static void register(String body, CoreClientData coreClientData, String grantType) {
		String beanName = grantType + BASE_NAME;
		if (!SpringUtils.containsBean(beanName)) {
			throw new ServiceException("Incorrect authorization type!");
		}
		IAuthStrategy instance = SpringUtils.getBean(beanName);
		instance.register(body, coreClientData);
	}

	LoginResult login(String body, CoreClientData coreClientData);

	void register(String body, CoreClientData coreClientData);

}
