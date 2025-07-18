package vn.tr.core.data.validator;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vn.tr.core.dao.model.CoreMenu;
import vn.tr.core.dao.service.CoreMenuService;
import vn.tr.core.data.dto.CoreMenuData;

@Component
@RequiredArgsConstructor
public class CoreMenuValidator implements Validator {
	
	private static final String ERROR_TARGET_NULL = "error.target.null";
	private final CoreMenuService coreMenuService;
	
	@Override
	public boolean supports(@NonNull Class<?> clazz) {
		// Chỉ áp dụng validator này cho class CoreMenuData và các lớp con của nó
		return CoreMenuData.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(@Nullable Object target, @Nullable Errors errors) {
		
		if (errors == null) {
			throw new IllegalArgumentException("Errors object cannot be null.");
		}
		if (target == null) {
			errors.reject(ERROR_TARGET_NULL, "Đối tượng CoreMenuData không được null.");
			return;
		}
		
		CoreMenuData menuData = (CoreMenuData) target;
		
		// --- VALIDATION CHUNG CHO CẢ CREATE VÀ UPDATE ---
		
		// 1. Kiểm tra vòng lặp cha-con: một menu không thể là cha của chính nó
		if (menuData.getId() != null && menuData.getId().equals(menuData.getParentId())) {
			errors.rejectValue("parentId", "SelfParent", "Một menu không thể là cha của chính nó.");
		}
		
		// 2. Kiểm tra sự tồn tại của menu cha
		if (menuData.getParentId() != null && !coreMenuService.existsById(menuData.getParentId())) {
			errors.rejectValue("parentId", "ParentNotFound", "Menu cha không tồn tại.");
		}
		
		// --- VALIDATION RIÊNG BIỆT ---
		
		// 3. Kiểm tra trùng lặp mã (code)
		// Spring validation không dễ để phân biệt giữa CREATE và UPDATE,
		// nên ta cần một chút logic để xử lý.
		if (StringUtils.isNotBlank(menuData.getAppCode()) && StringUtils.isNotBlank(menuData.getCode())) {
			// Lấy menu hiện có trong DB bằng mã
			var existingMenuOpt = coreMenuService.findByCodeSafely(menuData.getAppCode(), menuData.getCode());
			
			if (existingMenuOpt.isPresent()) {
				CoreMenu existingMenu = existingMenuOpt.get();
				if (menuData.getId() == null) {
					// Trường hợp CREATE: nếu tìm thấy -> lỗi trùng lặp
					errors.rejectValue("code", "Unique", "Mã menu đã tồn tại trong ứng dụng này.");
				} else {
					// Trường hợp UPDATE: nếu tìm thấy một menu khác có cùng mã -> lỗi trùng lặp
					if (!existingMenu.getId().equals(menuData.getId())) {
						errors.rejectValue("code", "Unique", "Mã menu đã tồn tại trong ứng dụng này.");
					}
				}
			}
		}
		
		// Thêm các quy tắc validation khác nếu cần
		// Ví dụ: kiểm tra permission_code có tồn tại không
		// if (menuData.getPermissionCode() != null && !corePermissionService.existsByCode(menuData.getPermissionCode())) {
		//     errors.rejectValue("permissionCode", "PermissionNotFound", "Mã quyền không tồn tại.");
		// }
	}
}
