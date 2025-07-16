package vn.tr.core.data.validator;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vn.tr.core.dao.service.CoreUserService;
import vn.tr.core.data.dto.CoreUserData;

@Component
@RequiredArgsConstructor
public class CoreUserValidator implements Validator {
	
	private static final String ERROR_TARGET_NULL = "error.target.null";
	private final CoreUserService coreUserService;
	
	@Override
	public boolean supports(@NonNull Class<?> clazz) {
		return CoreUserData.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(@Nullable Object target, @Nullable Errors errors) {
		if (errors == null) {
			throw new IllegalArgumentException("Errors object cannot be null.");
		}
		if (target == null) {
			errors.reject(ERROR_TARGET_NULL, "Đối tượng CoreUserData không được null.");
			return;
		}
		
		CoreUserData userDto = (CoreUserData) target;
		
		if (StringUtils.isBlank(userDto.getUsername())) {
			errors.rejectValue("username", "NotNull", "Tên đăng nhập không được để trống.");
		} else {
			if (userDto.getId() == null && coreUserService.existsByUsernameIgnoreCase(userDto.getUsername())) {
				errors.rejectValue("username", "Unique", "Tên đăng nhập đã tồn tại.");
			}
		}
		
		if (StringUtils.isBlank(userDto.getEmail())) {
			errors.rejectValue("email", "NotNull", "Email không được để trống.");
		} else {
			if (coreUserService.existsByIdNotAndUsernameIgnoreCase(userDto.getId(), userDto.getEmail())) {
				errors.rejectValue("email", "Unique", "Email đã tồn tại.");
			}
		}
		
		if (userDto.getId() == null && StringUtils.isBlank(userDto.getPassword())) {
			errors.rejectValue("password", "NotNull", "Mật khẩu không được để trống khi tạo mới.");
		}
		
		if (StringUtils.isBlank(userDto.getFullName())) {
			errors.rejectValue("fullName", "NotNull", "Họ và tên không được để trống.");
		}
	}
}
