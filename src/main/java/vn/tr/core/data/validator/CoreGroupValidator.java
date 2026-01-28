package vn.tr.core.data.validator;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.core.dao.service.CoreGroupService;
import vn.tr.core.data.dto.CoreGroupData;

@Component
@RequiredArgsConstructor
public class CoreGroupValidator implements Validator {

    private static final String ERROR_APP_ID_NOT_FOUND = "error.app.id.notfound";
    private static final String ERROR_CODE_DUPLICATE = "error.code.duplicate";
    private static final String ERROR_NAME_DUPLICATE = "error.name.duplicate";
    private static final String ERROR_PARENT_SELF = "error.parent.self";
    private static final String ERROR_PARENT_NOT_FOUND = "error.parent.notfound";
    private static final String ERROR_TARGET_NULL = "error.target.null";
    private final CoreGroupService coreGroupService;

    @Override
    public boolean supports(Class<?> clazz) {
        return CoreGroupData.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (errors == null) {
            throw new IllegalArgumentException("Errors object cannot be null.");
        }
        if (target == null) {
            errors.reject(ERROR_TARGET_NULL, "Đối tượng CoreGroupData không được null.");
            return;
        }

        CoreGroupData data = (CoreGroupData) target;

        String appCode = LoginHelper.getAppCode();
//		if (appCode == null) {
//			errors.reject(ERROR_APP_ID_NOT_FOUND, "Không thể xác định ứng dụng hiện tại.");
//			return;
//		}

        if (StrUtil.isNotBlank(data.getCode()) && isDuplicate(data.getId(), data.getCode(), appCode, true)) {
            errors.rejectValue("code", ERROR_CODE_DUPLICATE, "Mã đã tồn tại.");
        }

        if (StrUtil.isNotBlank(data.getName()) && isDuplicate(data.getId(), data.getName(), appCode, false)) {
            errors.rejectValue("name", ERROR_NAME_DUPLICATE, "Tên đã tồn tại.");
        }

        if (data.getParentId() != null) {
            if (data.getId() != null && data.getId().equals(data.getParentId())) {
                errors.rejectValue("parentId", ERROR_PARENT_SELF, "Không thể chọn chính nó làm nhóm cha.");
            } else if (!coreGroupService.existsByIdAndAppCode(data.getParentId(), appCode)) {
                errors.rejectValue("parentId", ERROR_PARENT_NOT_FOUND, "Nhóm cha không tồn tại hoặc không hợp lệ.");
            }
        }
    }

    private boolean isDuplicate(Long id, String value, String appCode, boolean isCode) {
        if (id != null) {
            return isCode
                    ? coreGroupService.existsByIdNotAndCodeIgnoreCaseAndAppCode(id, value, appCode)
                    : coreGroupService.existsByIdNotAndNameIgnoreCaseAndAppCode(id, value, appCode);
        }
        return isCode
                ? coreGroupService.existsByCodeIgnoreCaseAndAppCode(value, appCode)
                : coreGroupService.existsByNameIgnoreCaseAndAppCode(value, appCode);
    }
}
