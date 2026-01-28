package vn.tr.core.security.service.strategy;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.tr.common.core.constant.Constants;
import vn.tr.common.core.constant.GlobalConstants;
import vn.tr.common.core.domain.model.PasswordLoginBody;
import vn.tr.common.core.domain.model.RegisterBody;
import vn.tr.common.core.exception.user.CaptchaException;
import vn.tr.common.core.exception.user.CaptchaExpireException;
import vn.tr.common.core.utils.MessageUtils;
import vn.tr.common.redis.utils.RedisUtils;
import vn.tr.common.web.config.properties.CaptchaProperties;
import vn.tr.core.dao.service.CoreUserAppService;
import vn.tr.core.dao.service.CoreUserService;
import vn.tr.core.security.service.IAuthStrategy;

@Slf4j
@Service("password2captcha" + IAuthStrategy.BASE_NAME)
public class Password2CaptchaAuthStrategy extends BasePasswordStrategy {
	
	private final CaptchaProperties captchaProperties;
	
	public Password2CaptchaAuthStrategy(CoreUserService coreUserService, CoreUserAppService coreUserAppService, CaptchaProperties captchaProperties) {
		super(coreUserService, coreUserAppService);
		this.captchaProperties = captchaProperties;
	}
	
	@Override
	protected void preLoginValidate(PasswordLoginBody passwordBody) {
		log.debug("Thực hiện validate captcha cho đăng nhập...");
		if (captchaProperties.getEnable()) {
			validateCaptcha(passwordBody.getUsername(), passwordBody.getCode(), passwordBody.getUuid());
		}
	}
	
	@Override
	protected void preRegisterValidate(RegisterBody registerBody) {
		log.debug("Thực hiện validate captcha cho đăng ký...");
		if (captchaProperties.getEnable()) {
			validateCaptcha(registerBody.getUsername(), registerBody.getCode(), registerBody.getUuid());
		}
	}
	
	private void validateCaptcha(String username, String code, String uuid) {
		String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + StrUtil.blankToDefault(uuid, "");
		String captcha = RedisUtils.getCacheObject(verifyKey);
		RedisUtils.deleteObject(verifyKey);
		if (captcha == null) {
			coreUserService.recordLoginInfo(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
			throw new CaptchaExpireException();
		}
		if (!code.equalsIgnoreCase(captcha)) {
			coreUserService.recordLoginInfo(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error"));
			throw new CaptchaException();
		}
	}
}
