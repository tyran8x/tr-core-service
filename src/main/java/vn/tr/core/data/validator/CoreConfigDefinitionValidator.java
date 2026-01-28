package vn.tr.core.data.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.core.dao.service.CoreConfigDefinitionService;
import vn.tr.core.data.dto.CoreConfigDefinitionData;

/**
 * Validator tùy chỉnh cho CoreConfigDefinitionData. Chịu trách nhiệm kiểm tra
 * các quy tắc nghiệp vụ như sự trùng lặp của `key` trong ngữ cảnh của một ứng
 * dụng (appCode).
 *
 * @author tyran8x
 * @version 2.0
 */
@Component
@RequiredArgsConstructor
public class CoreConfigDefinitionValidator implements Validator {

    private final CoreConfigDefinitionService definitionService;

    /**
     * Xác định xem validator này có hỗ trợ lớp được cung cấp hay không.
     *
     * @param clazz Lớp cần kiểm tra.
     *
     * @return true nếu lớp là CoreConfigDefinitionData hoặc lớp con của nó.
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return CoreConfigDefinitionData.class.isAssignableFrom(clazz);
    }

    /**
     * Thực hiện validation trên đối tượng target.
     *
     * @param target Đối tượng cần validate (dự kiến là
     * CoreConfigDefinitionData).
     * @param errors Đối tượng để ghi lại các lỗi validation.
     */
    @Override
    public void validate(Object target, Errors errors) {
        CoreConfigDefinitionData data = (CoreConfigDefinitionData) target;

        // Lấy ngữ cảnh ứng dụng. Logic này đảm bảo App Admin không thể giả mạo appCode,
        // trong khi Super Admin có thể chỉ định nó (nếu DTO yêu cầu).
        // Tuy nhiên, logic kiểm tra quyền cuối cùng nằm ở tầng Business.
        // Validator chỉ tập trung vào tính hợp lệ của dữ liệu.
        String appCodeContext;
        if (LoginHelper.isSuperAdmin()) {
            // Đối với Super Admin, appCode được lấy từ DTO (đã được @NotBlank đảm bảo).
            appCodeContext = data.getAppCode();
        } else {
            // Đối với App Admin, luôn sử dụng appCode từ token để đảm bảo an toàn.
            appCodeContext = LoginHelper.getAppCode();
        }

        // Kiểm tra sự trùng lặp của 'key'
        if (data.getKey() != null && !data.getKey().isBlank()) {
            boolean isDuplicate;
            if (data.getId() != null) {
                // Trường hợp CẬP NHẬT: Kiểm tra xem có 'key' nào khác (không phải của bản ghi này)
                // đã tồn tại trong cùng app hay không.
                isDuplicate = definitionService.existsByIdNotAndKeyIgnoreCaseAndAppCode(
                        data.getId(),
                        data.getKey(),
                        appCodeContext);
            } else {
                // Trường hợp TẠO MỚI: Kiểm tra xem 'key' đã tồn tại trong app hay chưa.
                isDuplicate = definitionService.existsByKeyIgnoreCaseAndAppCode(
                        data.getKey(),
                        appCodeContext);
            }

            if (isDuplicate) {
                errors.rejectValue("key", "error.code.duplicate",
                        String.format("Key '%s' đã tồn tại trong ứng dụng này.", data.getKey()));
            }
        }

        // Có thể thêm các quy tắc validation phức tạp khác ở đây, ví dụ:
        // - Kiểm tra xem `validationRules` có phải là một JSON hợp lệ hay không.
        // - Kiểm tra xem `defaultValue` có tuân thủ `dataType` và `validationRules` hay không.
    }
}
