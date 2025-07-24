package vn.tr.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.common.log.annotation.Log;
import vn.tr.common.log.enums.BusinessType;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.common.web.data.dto.BulkOperationResult;
import vn.tr.common.web.data.dto.DeleteData;
import vn.tr.common.web.utils.PagedResult;
import vn.tr.core.business.CoreUserTypeBusiness;
import vn.tr.core.data.criteria.CoreUserTypeSearchCriteria;
import vn.tr.core.data.dto.CoreUserTypeData;
import vn.tr.core.data.validator.CoreUserTypeValidator;

/**
 * Controller quản lý các nghiệp vụ cho Loại người dùng (CoreUserType).
 * Các thao tác ghi (CUD) yêu cầu quyền Super Admin.
 *
 * @author tyran8x
 * @version 2.0
 */
@RestController
@RequestMapping("/user-types")
@RequiredArgsConstructor
public class CoreUserTypeController {
	
	private final CoreUserTypeBusiness coreUserTypeBusiness;
	private final CoreUserTypeValidator coreUserTypeValidator;
	
	/**
	 * Đăng ký validator tùy chỉnh cho CoreUserTypeData.
	 *
	 * @param binder WebDataBinder
	 */
	@InitBinder("coreUserTypeData")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(coreUserTypeValidator);
	}
	
	/**
	 * Tạo mới một loại người dùng. Yêu cầu quyền Super Admin.
	 *
	 * @param data Dữ liệu cần tạo.
	 *
	 * @return Dữ liệu sau khi tạo thành công.
	 */
	@PostMapping
	@Log(title = "Tạo mới Loại người dùng", businessType = BusinessType.INSERT)
	public R<CoreUserTypeData> create(@Valid @RequestBody CoreUserTypeData data) {
		String appCodeContext = LoginHelper.getAppCode();
		return R.ok(coreUserTypeBusiness.create(data, appCodeContext));
	}
	
	/**
	 * Cập nhật thông tin một loại người dùng. Yêu cầu quyền Super Admin.
	 *
	 * @param id   ID của loại người dùng cần cập nhật.
	 * @param data Dữ liệu mới.
	 *
	 * @return Dữ liệu sau khi cập nhật.
	 */
	@PutMapping("/{id}")
	@Log(title = "Cập nhật Loại người dùng", businessType = BusinessType.UPDATE)
	public R<CoreUserTypeData> update(@PathVariable Long id, @Valid @RequestBody CoreUserTypeData data) {
		String appCodeContext = LoginHelper.getAppCode();
		return R.ok(coreUserTypeBusiness.update(id, data, appCodeContext));
	}
	
	/**
	 * Xóa một loại người dùng (xóa mềm). Yêu cầu quyền Super Admin.
	 *
	 * @param id ID của loại người dùng cần xóa.
	 *
	 * @return R.ok() nếu thành công.
	 */
	@DeleteMapping("/{id}")
	@Log(title = "Xóa Loại người dùng", businessType = BusinessType.DELETE)
	public R<Void> delete(@PathVariable Long id) {
		coreUserTypeBusiness.delete(id, LoginHelper.isSuperAdmin());
		return R.ok();
	}
	
	/**
	 * Xóa hàng loạt loại người dùng (xóa mềm). Yêu cầu quyền Super Admin.
	 *
	 * @param deleteData Đối tượng chứa danh sách các ID cần xóa.
	 *
	 * @return Báo cáo chi tiết về kết quả xóa của từng ID.
	 */
	@DeleteMapping
	@Log(title = "Xóa hàng loạt Loại người dùng", businessType = BusinessType.DELETE)
	public R<BulkOperationResult<Long>> bulkDelete(@Valid @RequestBody DeleteData deleteData) {
		return R.ok(coreUserTypeBusiness.bulkDelete(deleteData.getIds(), LoginHelper.isSuperAdmin()));
	}
	
	/**
	 * Lấy thông tin chi tiết của một loại người dùng.
	 *
	 * @param id ID cần tìm.
	 *
	 * @return Dữ liệu chi tiết.
	 */
	@GetMapping("/{id}")
	@Log(title = "Lấy chi tiết Loại người dùng", businessType = BusinessType.DETAIL)
	public R<CoreUserTypeData> findById(@PathVariable long id) {
		return R.ok(coreUserTypeBusiness.findById(id));
	}
	
	/**
	 * Tìm kiếm và trả về danh sách loại người dùng có phân trang.
	 *
	 * @param criteria Các tiêu chí để tìm kiếm.
	 *
	 * @return Kết quả phân trang.
	 */
	@GetMapping
	@Log(title = "Tìm kiếm Loại người dùng (phân trang)", businessType = BusinessType.FINDALL)
	public R<PagedResult<CoreUserTypeData>> findAll(CoreUserTypeSearchCriteria criteria) {
		return R.ok(coreUserTypeBusiness.findAll(criteria));
	}
}
