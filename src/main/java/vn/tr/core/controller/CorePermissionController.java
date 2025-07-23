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
import vn.tr.core.business.CorePermissionBusiness;
import vn.tr.core.data.criteria.CorePermissionSearchCriteria;
import vn.tr.core.data.dto.CorePermissionData;
import vn.tr.core.data.validator.CorePermissionValidator;

/**
 * Controller quản lý các nghiệp vụ cho CorePermission.
 *
 * @author tyran8x
 * @version 2.0
 */
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class CorePermissionController {
	
	private final CorePermissionBusiness corePermissionBusiness;
	private final CorePermissionValidator corePermissionValidator;
	
	@InitBinder("corePermissionData")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(corePermissionValidator);
	}
	
	@PostMapping
	@Log(title = "Tạo mới Quyền hạn", businessType = BusinessType.INSERT)
	public R<CorePermissionData> create(@Valid @RequestBody CorePermissionData data) {
		String appCodeContext = LoginHelper.getAppCode();
		return R.ok(corePermissionBusiness.create(data, appCodeContext));
	}
	
	@PutMapping("/{id}")
	@Log(title = "Cập nhật Quyền hạn", businessType = BusinessType.UPDATE)
	public R<CorePermissionData> update(@PathVariable Long id, @Valid @RequestBody CorePermissionData data) {
		String appCodeContext = LoginHelper.getAppCode();
		return R.ok(corePermissionBusiness.update(id, data, appCodeContext));
	}
	
	@DeleteMapping("/{id}")
	@Log(title = "Xóa Quyền hạn", businessType = BusinessType.DELETE)
	public R<Void> delete(@PathVariable Long id) {
		String appCodeContext = LoginHelper.getAppCode();
		corePermissionBusiness.delete(id, appCodeContext);
		return R.ok();
	}
	
	@DeleteMapping
	@Log(title = "Xóa hàng loạt Quyền hạn", businessType = BusinessType.DELETE)
	public R<BulkOperationResult<Long>> bulkDelete(@Valid @RequestBody DeleteData deleteData) {
		String appCodeContext = LoginHelper.getAppCode();
		return R.ok(corePermissionBusiness.bulkDelete(deleteData.getIds(), appCodeContext));
	}
	
	@GetMapping("/{id}")
	@Log(title = "Lấy chi tiết Quyền hạn", businessType = BusinessType.DETAIL)
	public R<CorePermissionData> findById(@PathVariable long id) {
		String appCodeContext = LoginHelper.getAppCode();
		return R.ok(corePermissionBusiness.findById(id, appCodeContext));
	}
	
	@GetMapping
	@Log(title = "Tìm kiếm Quyền hạn (phân trang)", businessType = BusinessType.FINDALL)
	public R<PagedResult<CorePermissionData>> findAll(CorePermissionSearchCriteria criteria) {
		String appCodeContext = LoginHelper.getAppCode();
		return R.ok(corePermissionBusiness.findAll(criteria, appCodeContext));
	}
}
