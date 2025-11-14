package vn.tr.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.common.core.exception.base.InvalidEntityException;
import vn.tr.common.log.annotation.Log;
import vn.tr.common.log.enums.BusinessType;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.common.web.annotation.AppCode;
import vn.tr.common.web.data.dto.BulkOperationResult;
import vn.tr.common.web.data.dto.DeleteData;
import vn.tr.common.web.utils.PagedResult;
import vn.tr.core.business.CoreClientBusiness;
import vn.tr.core.data.criteria.CoreClientSearchCriteria;
import vn.tr.core.data.dto.CoreClientData;
import vn.tr.core.data.validator.CoreClientValidator;

/**
 * Controller quản lý các nghiệp vụ cho CoreClient.
 * Mọi thao tác đều yêu cầu ngữ cảnh ứng dụng hợp lệ.
 *
 * @author tyran8x
 * @version 2.0
 */
@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class CoreClientController {
	
	private final CoreClientBusiness coreClientBusiness;
	private final CoreClientValidator coreClientValidator;
	
	@InitBinder("coreClientData")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(coreClientValidator);
	}
	
	/**
	 * Tạo mới một client.
	 *
	 * @param coreClientData Dữ liệu của client cần tạo.
	 *
	 * @return Dữ liệu của client sau khi tạo thành công.
	 */
	@PostMapping
	@Log(title = "Tạo mới Client", businessType = BusinessType.INSERT)
	public R<CoreClientData> create(@Valid @RequestBody CoreClientData coreClientData) {
		String appCodeContext = getWriteAppContext(coreClientData.getAppCode());
		return R.ok(coreClientBusiness.create(coreClientData, appCodeContext));
	}
	
	/**
	 * Helper để lấy ngữ cảnh ứng dụng cho các thao tác GHI (POST/PUT).
	 * Đây là nơi thực thi quy tắc nghiệp vụ về `appCode` cho Super Admin và App Admin.
	 *
	 * @param appCodeFromDto appCode được gửi trong body của DTO.
	 *
	 * @return appCode cuối cùng được sử dụng cho nghiệp vụ.
	 *
	 * @throws InvalidEntityException nếu Super Admin không cung cấp appCode.
	 */
	private String getWriteAppContext(String appCodeFromDto) {
		if (LoginHelper.isSuperAdmin()) {
			if (appCodeFromDto == null || appCodeFromDto.isBlank()) {
				// Đối với thao tác ghi, Super Admin bắt buộc phải cung cấp appCode trong body.
				throw new InvalidEntityException("Super Admin phải cung cấp 'appCode' trong request body.");
			}
			return appCodeFromDto;
		} else {
			// Đối với App Admin, luôn sử dụng appCode từ token đã được xác thực của họ.
			return null;
		}
	}
	
	/**
	 * Cập nhật thông tin một client.
	 *
	 * @param id             ID của client cần cập nhật.
	 * @param coreClientData Dữ liệu mới của client.
	 *
	 * @return Dữ liệu của client sau khi cập nhật.
	 */
	@PutMapping("/{id}")
	@Log(title = "Cập nhật Client", businessType = BusinessType.UPDATE)
	public R<CoreClientData> update(@PathVariable Long id, @Valid @RequestBody CoreClientData coreClientData) {
		String appCodeContext = getWriteAppContext(coreClientData.getAppCode());
		return R.ok(coreClientBusiness.update(id, coreClientData, appCodeContext));
	}
	
	/**
	 * Xóa một client duy nhất (xóa mềm).
	 *
	 * @param id ID của client cần xóa.
	 *
	 * @return R.ok() nếu thành công.
	 */
	@DeleteMapping("/{id}")
	@Log(title = "Xóa Client", businessType = BusinessType.DELETE)
	public R<Void> delete(@PathVariable Long id, @AppCode String appCodeContext) {
		if (LoginHelper.isSuperAdmin() && (appCodeContext == null || appCodeContext.isBlank())) {
			throw new InvalidEntityException("Super Admin phải chỉ định 'appCode' qua query parameter khi xóa Client.");
		}
		coreClientBusiness.delete(id, appCodeContext);
		return R.ok();
	}
	
	/**
	 * Xóa hàng loạt client (xóa mềm).
	 *
	 * @param deleteData Đối tượng chứa danh sách các ID cần xóa.
	 *
	 * @return Một báo cáo chi tiết về kết quả xóa của từng ID.
	 */
	@DeleteMapping
	@Log(title = "Xóa hàng loạt Client", businessType = BusinessType.DELETE)
	public R<BulkOperationResult<Long>> bulkDelete(@Valid @RequestBody DeleteData deleteData, @AppCode String appCodeContext) {
		return R.ok(coreClientBusiness.bulkDelete(deleteData.getIds(), appCodeContext));
	}
	
	/**
	 * Lấy thông tin chi tiết của một client.
	 *
	 * @param id ID của client.
	 *
	 * @return Dữ liệu chi tiết của client.
	 */
	@GetMapping("/{id}")
	@Log(title = "Lấy chi tiết Client", businessType = BusinessType.DETAIL)
	public R<CoreClientData> findById(@PathVariable long id, @AppCode String appCodeContext) {
		return R.ok(coreClientBusiness.findById(id, appCodeContext));
	}
	
	/**
	 * Tìm kiếm và trả về danh sách client có phân trang.
	 *
	 * @param criteria Các tiêu chí tìm kiếm.
	 *
	 * @return Kết quả phân trang của danh sách client.
	 */
	@GetMapping
	@Log(title = "Tìm kiếm Client (phân trang)", businessType = BusinessType.FINDALL)
	public R<PagedResult<CoreClientData>> findAll(CoreClientSearchCriteria criteria, @AppCode String appCodeContext) {
		return R.ok(coreClientBusiness.findAll(criteria, appCodeContext));
	}
}
