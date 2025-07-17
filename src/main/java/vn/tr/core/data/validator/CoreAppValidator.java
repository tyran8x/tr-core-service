package vn.tr.core.data.validator;

import cn.hutool.core.util.StrUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.core.dao.service.CoreAppService;
import vn.tr.core.data.dto.CoreAppData;

@Component
@RequiredArgsConstructor
public class CoreAppValidator implements Validator {
	
	private static final String ERROR_APP_ID_NOT_FOUND = "error.app.id.notfound";
	private static final String ERROR_CODE_DUPLICATE = "error.code.duplicate";
	private static final String ERROR_NAME_DUPLICATE = "error.name.duplicate";
	private static final String ERROR_TARGET_NULL = "error.target.null";
	private final CoreAppService coreAppService;
	
	@Override
	public boolean supports(@NonNull Class<?> clazz) {
		return CoreAppData.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(@Nullable Object target, @Nullable Errors errors) {
		if (errors == null) {
			throw new IllegalArgumentException("Errors object cannot be null.");
		}
		if (target == null) {
			errors.reject(ERROR_TARGET_NULL, "Đối tượng CoreAppData không được null.");
			return;
		}
		
		CoreAppData data = (CoreAppData) target;
		
		String appCode = LoginHelper.getAppCode();
//		if (appCode == null) {
//			errors.reject(ERROR_APP_ID_NOT_FOUND, "Không thể xác định ứng dụng hiện tại.");
//			return;
//		}
		
		if (StrUtil.isNotBlank(data.getCode()) && isDuplicate(data.getId(), data.getCode(), true)) {
			errors.rejectValue("code", ERROR_CODE_DUPLICATE, "Mã đã tồn tại.");
		}
		
		if (StrUtil.isNotBlank(data.getName()) && isDuplicate(data.getId(), data.getName(), false)) {
			errors.rejectValue("name", ERROR_NAME_DUPLICATE, "Tên đã tồn tại.");
		}
		
	}
	
	private boolean isDuplicate(Long id, String value, boolean isCode) {
		if (id != null) {
			return isCode
					? coreAppService.existsByIdNotAndCodeIgnoreCase(id, value)
					: coreAppService.existsByIdNotAndNameIgnoreCase(id, value);
		}
		return isCode
				? coreAppService.existsByCodeIgnoreCase(value)
				: coreAppService.existsByNameIgnoreCase(value);
	}
}
