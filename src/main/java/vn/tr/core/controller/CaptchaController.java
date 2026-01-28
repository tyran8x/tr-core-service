package vn.tr.core.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.tr.common.core.constant.Constants;
import vn.tr.common.core.constant.GlobalConstants;
import vn.tr.common.core.domain.R;
import vn.tr.common.core.utils.SpringUtils;
import vn.tr.common.core.utils.reflect.ReflectUtils;
import vn.tr.common.mail.config.properties.MailProperties;
import vn.tr.common.mail.utils.MailUtils;
import vn.tr.common.ratelimiter.annotation.RateLimiter;
import vn.tr.common.ratelimiter.enums.LimitType;
import vn.tr.common.redis.utils.RedisUtils;
import vn.tr.common.web.config.properties.CaptchaProperties;
import vn.tr.common.web.enums.CaptchaType;
import vn.tr.core.data.CaptchaResult;

import java.time.Duration;

@SaIgnore
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth/captcha")
public class CaptchaController {
	
	private final CaptchaProperties captchaProperties;
	private final MailProperties mailProperties;
	
	@RateLimiter(key = "#email", time = 61, count = 1)
	@GetMapping("/resource/email/code")
	public R<Void> emailCode(@NotBlank(message = "{user.email.not.blank}") String email) {
		if (!mailProperties.getEnabled()) {
			return R.fail("Hệ thống hiện tại không kích hoạt chức năng email.！");
		}
		String key = GlobalConstants.CAPTCHA_CODE_KEY + email;
		String code = RandomUtil.randomNumbers(4);
		RedisUtils.setCacheObject(key, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION));
		try {
			MailUtils.sendText(email,
					"Mã xác minh",
					"Mã xác minh của bạn lần này là：" + code + ", có hiệu lực" + Constants.CAPTCHA_EXPIRATION + " phút");
		} catch (Exception e) {
			log.error("Email mã xác minh được gửi bất thường => {}", e.getMessage());
			return R.fail(e.getMessage());
		}
		return R.ok();
	}
	
	@RateLimiter(time = 61, count = 10, limitType = LimitType.IP)
	//	@PreAuthorize("@ss.hasPerm('sys:captcha:code')")
	@GetMapping("/code")
	public R<CaptchaResult> getCode() {
		CaptchaResult captchaResult = new CaptchaResult();
		boolean captchaEnabled = captchaProperties.getEnable();
		captchaResult.setIsEnable(captchaEnabled);
		if (!captchaEnabled) {
			return R.ok(captchaResult);
		}
		String uuid = IdUtil.simpleUUID();
		String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + uuid;
		CaptchaType captchaType = captchaProperties.getType();
		boolean isMath = CaptchaType.MATH == captchaType;
		Integer length = isMath ? captchaProperties.getNumberLength() : captchaProperties.getCharLength();
		CodeGenerator codeGenerator = ReflectUtils.newInstance(captchaType.getClazz(), length);
		AbstractCaptcha captcha = SpringUtils.getBean(captchaProperties.getCategory().getClazz());
		captcha.setGenerator(codeGenerator);
		captcha.createCode();
		String code = captcha.getCode();
		if (isMath) {
			ExpressionParser parser = new SpelExpressionParser();
			Expression exp = parser.parseExpression(StrUtil.replace(code, "=", ""));
			code = exp.getValue(String.class);
		}
		RedisUtils.setCacheObject(verifyKey, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION));
		captchaResult.setKey(uuid);
		captchaResult.setBase64(captcha.getImageBase64());
		return R.ok(captchaResult);
	}
	
}
