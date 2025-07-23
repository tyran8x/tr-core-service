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
import vn.tr.core.business.CoreAppBusiness;
import vn.tr.core.data.criteria.CoreAppSearchCriteria;
import vn.tr.core.data.dto.CoreAppData;
import vn.tr.core.data.validator.CoreAppValidator;

/**
 * Controller quản lý các nghiệp vụ cho CoreApp.
 * Các thao tác ghi (CUD) yêu cầu quyền Super Admin.
 * Đường dẫn API trong service này là tương đối, prefix chung được quản lý bởi API Gateway.
 *
 * @author tyran8x
 * @version 2.4
 */
@RestController
@RequestMapping("/apps")
@RequiredArgsConstructor
public class CoreAppController {
	
	private final CoreAppBusiness coreAppBusiness;
	private final CoreAppValidator coreAppValidator;
	
	/**
	 * Đăng ký validator tùy chỉnh cho các đối tượng CoreAppData.
	 *
	 * @param binder WebDataBinder sẽ được sử dụng cho data binding.
	 */
	@InitBinder("coreAppData")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(coreAppValidator);
	}
	
	/**
	 * Tạo mới một ứng dụng. Yêu cầu quyền Super Admin.
	 *
	 * @param coreAppData Dữ liệu của ứng dụng cần tạo.
	 *
	 * @return Dữ liệu của ứng dụng sau khi tạo thành công.
	 */
	@PostMapping
	@Log(title = "Tạo mới Ứng dụng", businessType = BusinessType.INSERT)
	public R<CoreAppData> create(@Valid @RequestBody CoreAppData coreAppData) {
		CoreAppData createdApp = coreAppBusiness.create(coreAppData, LoginHelper.isSuperAdmin());
		return R.ok(createdApp);
	}
	
	/**
	 * Cập nhật thông tin một ứng dụng. Yêu cầu quyền Super Admin.
	 *
	 * @param id          ID của ứng dụng cần cập nhật.
	 * @param coreAppData Dữ liệu mới của ứng dụng.
	 *
	 * @return Dữ liệu của ứng dụng sau khi cập nhật.
	 */
	@PutMapping("/{id}")
	@Log(title = "Cập nhật Ứng dụng", businessType = BusinessType.UPDATE)
	public R<CoreAppData> update(@PathVariable Long id, @Valid @RequestBody CoreAppData coreAppData) {
		CoreAppData updatedApp = coreAppBusiness.update(id, coreAppData, LoginHelper.isSuperAdmin());
		return R.ok(updatedApp);
	}
	
	/**
	 * Xóa một ứng dụng duy nhất (xóa mềm). Yêu cầu quyền Super Admin.
	 *
	 * @param id ID của ứng dụng cần xóa.
	 *
	 * @return R.ok() nếu thành công.
	 */
	@DeleteMapping("/{id}")
	@Log(title = "Xóa Ứng dụng", businessType = BusinessType.DELETE)
	public R<Void> delete(@PathVariable Long id) {
		coreAppBusiness.delete(id, LoginHelper.isSuperAdmin());
		return R.ok();
	}
	
	/**
	 * Xóa hàng loạt ứng dụng (xóa mềm). Yêu cầu quyền Super Admin.
	 * Trả về một báo cáo chi tiết về kết quả xóa của từng ID.
	 *
	 * @param deleteData Đối tượng chứa danh sách các ID cần xóa.
	 *
	 * @return Một đối tượng BulkOperationResult.
	 */
	@DeleteMapping
	@Log(title = "Xóa hàng loạt Ứng dụng", businessType = BusinessType.DELETE)
	public R<BulkOperationResult<Long>> bulkDelete(@Valid @RequestBody DeleteData deleteData) {
		BulkOperationResult<Long> result = coreAppBusiness.bulkDelete(deleteData.getIds(), LoginHelper.isSuperAdmin());
		return R.ok(result);
	}
	
	/**
	 * Lấy thông tin chi tiết của một ứng dụng.
	 *
	 * @param id ID của ứng dụng cần tìm.
	 *
	 * @return Dữ liệu chi tiết của ứng dụng.
	 */
	@GetMapping("/{id}")
	@Log(title = "Lấy chi tiết Ứng dụng", businessType = BusinessType.DETAIL)
	public R<CoreAppData> findById(@PathVariable long id) {
		CoreAppData appData = coreAppBusiness.findById(id);
		return R.ok(appData);
	}
	
	/**
	 * Tìm kiếm và trả về danh sách ứng dụng có phân trang.
	 *
	 * @param criteria Các tiêu chí để tìm kiếm.
	 *
	 * @return Kết quả phân trang của danh sách ứng dụng.
	 */
	@GetMapping
	@Log(title = "Tìm kiếm Ứng dụng (phân trang)", businessType = BusinessType.FINDALL)
	public R<PagedResult<CoreAppData>> findAll(CoreAppSearchCriteria criteria) {
		PagedResult<CoreAppData> pagedResult = coreAppBusiness.findAll(criteria);
		return R.ok(pagedResult);
	}
}
