package vn.tr.core.data.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.core.dao.service.CoreClientService;
import vn.tr.core.data.dto.CoreClientData;

/**
 * Validator tùy chỉnh cho CoreClientData.
 *
 * @author tyran8x
 * @version 2.0
 */
@Component
@RequiredArgsConstructor
public class CoreClientValidator implements Validator {
	
	private final CoreClientService coreClientService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return CoreClientData.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		CoreClientData data = (CoreClientData) target;
		
		// Ngữ cảnh ứng dụng được lấy từ token
		String appCodeContext = LoginHelper.getAppCode();
		
		// Chỉ validate clientId nếu nó được cung cấp (trường hợp update sẽ không đổi)
		if (data.getClientId() != null && !data.getClientId().isBlank()) {
			boolean isDuplicate;
			if (data.getId() != null) {
				// Trường hợp update
				isDuplicate = coreClientService.existsByIdNotAndClientIdAndAppCode(data.getId(), data.getClientId(), appCodeContext);
			} else {
				// Trường hợp create
				isDuplicate = coreClientService.existsByClientIdAndAppCode(data.getClientId(), appCodeContext);
			}
			if (isDuplicate) {
				errors.rejectValue("clientId", "error.code.duplicate", "Client ID đã tồn tại trong ứng dụng này.");
			}
		}
	}
}
