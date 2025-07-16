package vn.tr.core.data.validator;

import cn.hutool.core.util.StrUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.core.dao.service.CoreWorkSpaceItemService;
import vn.tr.core.data.dto.CoreWorkSpaceItemData;

@Component
@RequiredArgsConstructor
public class CoreWorkSpaceItemValidator implements Validator {
	
	private static final String ERROR_APP_ID_NOT_FOUND = "error.app.id.notfound";
	private static final String ERROR_CODE_DUPLICATE = "error.code.duplicate";
	private static final String ERROR_NAME_DUPLICATE = "error.name.duplicate";
	private static final String ERROR_PARENT_SELF = "error.parent.self";
	private static final String ERROR_PARENT_NOT_FOUND = "error.parent.notfound";
	private static final String ERROR_TARGET_NULL = "error.target.null";
	private final CoreWorkSpaceItemService coreWorkSpaceItemService;
	
	@Override
	public boolean supports(@NonNull Class<?> clazz) {
		return CoreWorkSpaceItemData.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(@Nullable Object target, @Nullable Errors errors) {
		if (errors == null) {
			throw new IllegalArgumentException("Errors object cannot be null.");
		}
		if (target == null) {
			errors.reject(ERROR_TARGET_NULL, "Đối tượng CoreWorkSpaceItemData không được null.");
			return;
		}
		
		CoreWorkSpaceItemData data = (CoreWorkSpaceItemData) target;
		
		String appCode = LoginHelper.getAppCode();
//		if (appCode == null) {
//			errors.reject(ERROR_APP_ID_NOT_FOUND, "Không thể xác định ứng dụng hiện tại.");
//			return;
//		}
		
		if (StrUtil.isNotBlank(data.getCode()) && isDuplicate(data.getId(), data.getCode(), appCode, true)) {
			errors.rejectValue("code", ERROR_CODE_DUPLICATE, "Mã nhóm đã tồn tại.");
		}
		
		if (StrUtil.isNotBlank(data.getName()) && isDuplicate(data.getId(), data.getName(), appCode, false)) {
			errors.rejectValue("name", ERROR_NAME_DUPLICATE, "Tên nhóm đã tồn tại.");
		}
		
		if (data.getParentId() != null) {
			if (data.getId() != null && data.getId().equals(data.getParentId())) {
				errors.rejectValue("parentId", ERROR_PARENT_SELF, "Không thể chọn chính nó làm nhóm cha.");
			} else if (!coreWorkSpaceItemService.existsByIdAndAppCode(data.getParentId(), appCode)) {
				errors.rejectValue("parentId", ERROR_PARENT_NOT_FOUND, "Nhóm cha không tồn tại hoặc không hợp lệ.");
			}
		}
	}
	
	private boolean isDuplicate(Long id, String value, String appCode, boolean isCode) {
		if (id != null) {
			return isCode
					? coreWorkSpaceItemService.existsByIdNotAndCodeIgnoreCaseAndAppCode(id, value, appCode)
					: coreWorkSpaceItemService.existsByIdNotAndNameIgnoreCaseAndAppCode(id, value, appCode);
		}
		return isCode
				? coreWorkSpaceItemService.existsByCodeIgnoreCaseAndAppCode(value, appCode)
				: coreWorkSpaceItemService.existsByNameIgnoreCaseAndAppCode(value, appCode);
	}
}
