package vn.tr.core.data.validator;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.core.dao.service.CoreRoleService;
import vn.tr.core.data.dto.CoreRoleData;

@Component
@RequiredArgsConstructor
public class CoreRoleValidator implements Validator {

    private static final String ERROR_APP_ID_NOT_FOUND = "error.app.id.notfound";
    private static final String ERROR_CODE_DUPLICATE = "error.code.duplicate";
    private static final String ERROR_NAME_DUPLICATE = "error.name.duplicate";
    private static final String ERROR_TARGET_NULL = "error.target.null";
    private final CoreRoleService coreRoleService;

    @Override
    public boolean supports(Class<?> clazz) {
        return CoreRoleData.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (errors == null) {
            throw new IllegalArgumentException("Errors object cannot be null.");
        }
        if (target == null) {
            errors.reject(ERROR_TARGET_NULL, "Đối tượng CoreRoleData không được null.");
            return;
        }

        CoreRoleData data = (CoreRoleData) target;

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

    }

    private boolean isDuplicate(Long id, String value, String appCode, boolean isCode) {
        if (id != null) {
            return isCode
                    ? coreRoleService.existsByIdNotAndCodeIgnoreCaseAndAppCode(id, value, appCode)
                    : coreRoleService.existsByIdNotAndNameIgnoreCaseAndAppCode(id, value, appCode);
        }
        return isCode
                ? coreRoleService.existsByCodeIgnoreCaseAndAppCode(value, appCode)
                : coreRoleService.existsByNameIgnoreCaseAndAppCode(value, appCode);
    }
}
