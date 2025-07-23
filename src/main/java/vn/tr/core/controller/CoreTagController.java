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
import vn.tr.core.business.CoreTagBusiness;
import vn.tr.core.data.criteria.CoreTagSearchCriteria;
import vn.tr.core.data.dto.CoreTagData;
import vn.tr.core.data.validator.CoreTagValidator;

import java.util.List;

/**
 * Controller quản lý CoreTag, một tài nguyên toàn cục.
 * Các thao tác ghi (Create, Update, Delete) yêu cầu quyền Super Admin.
 *
 * @author tyran8x
 * @version 2.1
 */
@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class CoreTagController {
	
	private final CoreTagBusiness coreTagBusiness;
	private final CoreTagValidator coreTagValidator;
	
	/**
	 * Đăng ký validator tùy chỉnh cho CoreTagData.
	 *
	 * @param binder WebDataBinder
	 */
	@InitBinder("coreTagData")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(coreTagValidator);
	}
	
	/**
	 * Tạo mới một thẻ tag. Yêu cầu quyền Super Admin.
	 *
	 * @param data Dữ liệu thẻ tag cần tạo.
	 *
	 * @return Dữ liệu thẻ tag sau khi tạo thành công.
	 */
	@PostMapping
	@Log(title = "Tạo mới Thẻ tag", businessType = BusinessType.INSERT)
	public R<CoreTagData> create(@Valid @RequestBody CoreTagData data) {
		return R.ok(coreTagBusiness.create(data, LoginHelper.isSuperAdmin()));
	}
	
	/**
	 * Cập nhật thông tin một thẻ tag. Yêu cầu quyền Super Admin.
	 *
	 * @param id   ID của thẻ tag cần cập nhật.
	 * @param data Dữ liệu mới.
	 *
	 * @return Dữ liệu sau khi cập nhật.
	 */
	@PutMapping("/{id}")
	@Log(title = "Cập nhật Thẻ tag", businessType = BusinessType.UPDATE)
	public R<CoreTagData> update(@PathVariable Long id, @Valid @RequestBody CoreTagData data) {
		return R.ok(coreTagBusiness.update(id, data, LoginHelper.isSuperAdmin()));
	}
	
	/**
	 * Xóa một thẻ tag (xóa mềm). Yêu cầu quyền Super Admin.
	 *
	 * @param id ID của thẻ tag cần xóa.
	 *
	 * @return R.ok() nếu thành công.
	 */
	@DeleteMapping("/{id}")
	@Log(title = "Xóa Thẻ tag", businessType = BusinessType.DELETE)
	public R<Void> delete(@PathVariable Long id) {
		coreTagBusiness.delete(id, LoginHelper.isSuperAdmin());
		return R.ok();
	}
	
	/**
	 * Xóa hàng loạt thẻ tag (xóa mềm). Yêu cầu quyền Super Admin.
	 *
	 * @param deleteData Đối tượng chứa danh sách các ID cần xóa.
	 *
	 * @return Báo cáo chi tiết về kết quả xóa của từng ID.
	 */
	@DeleteMapping
	@Log(title = "Xóa hàng loạt Thẻ tag", businessType = BusinessType.DELETE)
	public R<BulkOperationResult<Long>> bulkDelete(@Valid @RequestBody DeleteData deleteData) {
		return R.ok(coreTagBusiness.bulkDelete(deleteData.getIds(), LoginHelper.isSuperAdmin()));
	}
	
	/**
	 * Lấy thông tin chi tiết của một thẻ tag.
	 *
	 * @param id ID cần tìm.
	 *
	 * @return Dữ liệu chi tiết.
	 */
	@GetMapping("/{id}")
	@Log(title = "Lấy chi tiết Thẻ tag", businessType = BusinessType.DETAIL)
	public R<CoreTagData> findById(@PathVariable long id) {
		return R.ok(coreTagBusiness.findById(id));
	}
	
	/**
	 * Tìm kiếm và trả về danh sách thẻ tag có phân trang.
	 *
	 * @param criteria Các tiêu chí để tìm kiếm.
	 *
	 * @return Kết quả phân trang.
	 */
	@GetMapping
	@Log(title = "Tìm kiếm Thẻ tag (phân trang)", businessType = BusinessType.FINDALL)
	public R<PagedResult<CoreTagData>> findAll(CoreTagSearchCriteria criteria) {
		return R.ok(coreTagBusiness.findAll(criteria));
	}
	
	/**
	 * Lấy toàn bộ danh sách thẻ tag (không phân trang), thường dùng cho các dropdown.
	 *
	 * @param criteria Các tiêu chí để lọc (nếu có).
	 *
	 * @return Danh sách đầy đủ các thẻ tag.
	 */
	@GetMapping("/list")
	@Log(title = "Lấy danh sách Thẻ tag", businessType = BusinessType.FINDALL)
	public R<List<CoreTagData>> getAll(CoreTagSearchCriteria criteria) {
		// Giả sử có một phương thức getAll trong Business
		// return R.ok(coreTagBusiness.getAll(criteria));
		// Nếu không, có thể lấy trang đầu tiên với kích thước lớn
		criteria.setPage(0);
		criteria.setSize(1000); // Giới hạn hợp lý
		return R.ok(coreTagBusiness.findAll(criteria).getContent());
	}
}
