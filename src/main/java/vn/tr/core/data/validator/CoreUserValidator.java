package vn.tr.core.data.validator;

import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vn.tr.core.dao.service.CoreUserService;
import vn.tr.core.data.dto.CoreUserData;

import java.util.Objects;

@Component
public class CoreUserValidator implements Validator {
	@Autowired
	protected MessageSource messageSource;
	@Autowired
	protected CoreUserService coreUserService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return CoreUserData.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		CoreUserData object = (CoreUserData) target;
		if (checkEmailExits(object.getId(), object.getUsername())) {
			errors.rejectValue("email", "error.email", new Object[]{"email"}, "Email đã tồn tại");
		}
	}
	
	private boolean checkEmailExits(Long id, String email) {
		if (CharSequenceUtil.isNotBlank(email)) {
			if (Objects.nonNull(id)) {
				return coreUserService.existsByIdNotAndUsernameIgnoreCaseAndDaXoaFalse(id, email);
			}
			return coreUserService.existsByUsernameIgnoreCaseAndDaXoaFalse(email);
		}
		return false;
	}
}
