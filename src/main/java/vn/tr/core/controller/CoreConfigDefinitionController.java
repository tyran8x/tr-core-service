package vn.tr.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.common.log.annotation.Log;
import vn.tr.common.log.enums.BusinessType;
import vn.tr.common.web.annotation.AppCode;
import vn.tr.common.web.data.dto.BulkOperationResult;
import vn.tr.common.web.data.dto.DeleteData;
import vn.tr.common.web.utils.PagedResult;
import vn.tr.core.business.CoreConfigDefinitionBusiness;
import vn.tr.core.data.criteria.CoreConfigDefinitionSearchCriteria;
import vn.tr.core.data.dto.CoreConfigDefinitionData;
import vn.tr.core.data.validator.CoreConfigDefinitionValidator;

/**
 * Controller quản lý các nghiệp vụ cho Định nghĩa Cấu hình (CoreConfigDefinition).
 * Mọi thao tác đều yêu cầu ngữ cảnh ứng dụng hợp lệ.
 *
 * @author tyran8x
 * @version 2.0
 */
@RestController
@RequestMapping("/config-definitions")
@RequiredArgsConstructor
public class CoreConfigDefinitionController {
	
	private final CoreConfigDefinitionBusiness coreConfigDefinitionBusiness;
	private final CoreConfigDefinitionValidator coreConfigDefinitionValidator;
	
	@InitBinder("coreConfigDefinitionData")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(coreConfigDefinitionValidator);
	}
	
	/**
	 * Tạo mới một định nghĩa cấu hình.
	 *
	 * @param data    Dữ liệu của định nghĩa cần tạo.
	 * @param appCode Ngữ cảnh ứng dụng, được resolver điền.
	 *
	 * @return Dữ liệu của định nghĩa sau khi tạo thành công.
	 */
	@PostMapping
	@Log(title = "Tạo mới Định nghĩa Cấu hình", businessType = BusinessType.INSERT)
	public R<CoreConfigDefinitionData> create(@Valid @RequestBody CoreConfigDefinitionData data, @AppCode String appCode) {
		return R.ok(coreConfigDefinitionBusiness.create(data, appCode));
	}
	
	/**
	 * Cập nhật thông tin một định nghĩa cấu hình.
	 *
	 * @param id      ID của định nghĩa cần cập nhật.
	 * @param data    Dữ liệu mới.
	 * @param appCode Ngữ cảnh ứng dụng.
	 *
	 * @return Dữ liệu sau khi cập nhật.
	 */
	@PutMapping("/{id}")
	@Log(title = "Cập nhật Định nghĩa Cấu hình", businessType = BusinessType.UPDATE)
	public R<CoreConfigDefinitionData> update(@PathVariable Long id, @Valid @RequestBody CoreConfigDefinitionData data, @AppCode String appCode) {
		return R.ok(coreConfigDefinitionBusiness.update(id, data, appCode));
	}
	
	/**
	 * Xóa một định nghĩa cấu hình (xóa mềm).
	 *
	 * @param id      ID của định nghĩa cần xóa.
	 * @param appCode Ngữ cảnh ứng dụng.
	 *
	 * @return R.ok() nếu thành công.
	 */
	@DeleteMapping("/{id}")
	@Log(title = "Xóa Định nghĩa Cấu hình", businessType = BusinessType.DELETE)
	public R<Void> delete(@PathVariable Long id, @AppCode String appCode) {
		coreConfigDefinitionBusiness.delete(id, appCode);
		return R.ok();
	}
	
	/**
	 * Xóa hàng loạt định nghĩa cấu hình (xóa mềm).
	 *
	 * @param deleteData Đối tượng chứa danh sách các ID cần xóa.
	 * @param appCode    Ngữ cảnh ứng dụng.
	 *
	 * @return Báo cáo chi tiết về kết quả xóa của từng ID.
	 */
	@DeleteMapping
	@Log(title = "Xóa hàng loạt Định nghĩa Cấu hình", businessType = BusinessType.DELETE)
	public R<BulkOperationResult<Long>> bulkDelete(@Valid @RequestBody DeleteData deleteData, @AppCode String appCode) {
		return R.ok(coreConfigDefinitionBusiness.bulkDelete(deleteData.getIds(), appCode));
	}
	
	/**
	 * Lấy thông tin chi tiết của một định nghĩa cấu hình.
	 *
	 * @param id      ID của định nghĩa.
	 * @param appCode Ngữ cảnh ứng dụng.
	 *
	 * @return Dữ liệu chi tiết.
	 */
	@GetMapping("/{id}")
	@Log(title = "Lấy chi tiết Định nghĩa Cấu hình", businessType = BusinessType.DETAIL)
	public R<CoreConfigDefinitionData> findById(@PathVariable long id, @AppCode String appCode) {
		return R.ok(coreConfigDefinitionBusiness.findById(id, appCode));
	}
	
	/**
	 * Tìm kiếm và trả về danh sách định nghĩa cấu hình có phân trang.
	 *
	 * @param criteria Các tiêu chí tìm kiếm.
	 * @param appCode  Ngữ cảnh ứng dụng.
	 *
	 * @return Kết quả phân trang.
	 */
	@GetMapping
	@Log(title = "Tìm kiếm Định nghĩa Cấu hình (phân trang)", businessType = BusinessType.FINDALL)
	public R<PagedResult<CoreConfigDefinitionData>> findAll(CoreConfigDefinitionSearchCriteria criteria, @AppCode String appCode) {
		return R.ok(coreConfigDefinitionBusiness.findAll(criteria, appCode));
	}
}
