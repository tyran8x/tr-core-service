package vn.tr.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.common.log.annotation.Log;
import vn.tr.common.log.enums.BusinessType;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.common.web.data.dto.BulkOperationResult;
import vn.tr.common.web.data.dto.DeleteData;
import vn.tr.common.web.utils.PagedResult;
import vn.tr.core.business.CoreModuleBusiness;
import vn.tr.core.data.criteria.CoreModuleSearchCriteria;
import vn.tr.core.data.dto.CoreModuleData;
import vn.tr.core.data.validator.CoreModuleValidator;

/**
 * Controller quản lý các nghiệp vụ cho CoreModule.
 * Mọi thao tác đều yêu cầu ngữ cảnh ứng dụng hợp lệ, được xác định từ token của người dùng.
 *
 * @author tyran8x
 * @version 2.0
 */
@RestController
@RequestMapping("/modules")
@RequiredArgsConstructor
@Slf4j
public class CoreModuleController {
	
	private final CoreModuleBusiness coreModuleBusiness;
	private final CoreModuleValidator coreModuleValidator;
	
	/**
	 * Đăng ký validator tùy chỉnh cho các đối tượng CoreModuleData.
	 *
	 * @param binder WebDataBinder sẽ được sử dụng cho data binding.
	 */
	@InitBinder("coreModuleData")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(coreModuleValidator);
	}
	
	/**
	 * Tạo mới một module.
	 * Ngữ cảnh ứng dụng được lấy từ token của người dùng đăng nhập.
	 *
	 * @param coreModuleData Dữ liệu của module cần tạo, nằm trong request body.
	 *
	 * @return Dữ liệu của module sau khi tạo thành công.
	 */
	@PostMapping
	@Log(title = "Tạo mới Module", businessType = BusinessType.INSERT)
	public R<CoreModuleData> create(@Valid @RequestBody CoreModuleData coreModuleData) {
		String appCodeContext = LoginHelper.getAppCode();
		return R.ok(coreModuleBusiness.create(coreModuleData, appCodeContext));
	}
	
	/**
	 * Cập nhật thông tin một module.
	 *
	 * @param id             ID của module cần cập nhật, lấy từ URL path.
	 * @param coreModuleData Dữ liệu mới của module, nằm trong request body.
	 *
	 * @return Dữ liệu của module sau khi cập nhật.
	 */
	@PutMapping("/{id}")
	@Log(title = "Cập nhật Module", businessType = BusinessType.UPDATE)
	public R<CoreModuleData> update(@PathVariable Long id, @Valid @RequestBody CoreModuleData coreModuleData) {
		String appCodeContext = LoginHelper.getAppCode();
		return R.ok(coreModuleBusiness.update(id, coreModuleData, appCodeContext));
	}
	
	/**
	 * Xóa một module duy nhất (xóa mềm).
	 *
	 * @param id ID của module cần xóa, lấy từ URL path.
	 *
	 * @return R.ok() nếu thành công, hoặc response lỗi 4xx/5xx nếu thất bại.
	 */
	@DeleteMapping("/{id}")
	@Log(title = "Xóa Module", businessType = BusinessType.DELETE)
	public R<Void> delete(@PathVariable Long id) {
		String appCodeContext = LoginHelper.getAppCode();
		coreModuleBusiness.delete(id, appCodeContext);
		return R.ok();
	}
	
	/**
	 * Xóa hàng loạt module (xóa mềm).
	 * API này sẽ trả về một báo cáo chi tiết về kết quả xóa của từng ID.
	 *
	 * @param deleteData Đối tượng chứa danh sách các ID cần xóa.
	 *
	 * @return Một đối tượng BulkOperationResult chứa danh sách thành công và thất bại.
	 */
	@DeleteMapping
	@Log(title = "Xóa hàng loạt Module", businessType = BusinessType.DELETE)
	public R<BulkOperationResult<Long>> bulkDelete(@Valid @RequestBody DeleteData deleteData) {
		String appCodeContext = LoginHelper.getAppCode();
		return R.ok(coreModuleBusiness.bulkDelete(deleteData.getIds(), appCodeContext));
	}
	
	/**
	 * Lấy thông tin chi tiết của một module.
	 *
	 * @param id ID của module, lấy từ URL path.
	 *
	 * @return Dữ liệu chi tiết của module.
	 */
	@GetMapping("/{id}")
	@Log(title = "Lấy chi tiết Module", businessType = BusinessType.DETAIL)
	public R<CoreModuleData> findById(@PathVariable long id) {
		String appCodeContext = LoginHelper.getAppCode();
		return R.ok(coreModuleBusiness.findById(id, appCodeContext));
	}
	
	/**
	 * Tìm kiếm và trả về danh sách module có phân trang.
	 *
	 * @param criteria Các tiêu chí tìm kiếm, truyền qua query params.
	 *
	 * @return Kết quả phân trang của danh sách module.
	 */
	@GetMapping
	@Log(title = "Tìm kiếm Module (phân trang)", businessType = BusinessType.FINDALL)
	public R<PagedResult<CoreModuleData>> findAll(CoreModuleSearchCriteria criteria) {
		String appCodeContext = LoginHelper.getAppCode();
		log.info("CoreModuleSearchCriteria: {}", criteria.getSearch());
		return R.ok(coreModuleBusiness.findAll(criteria, appCodeContext));
	}
}
