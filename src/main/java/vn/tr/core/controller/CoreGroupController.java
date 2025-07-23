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
import vn.tr.core.business.CoreGroupBusiness;
import vn.tr.core.data.criteria.CoreGroupSearchCriteria;
import vn.tr.core.data.dto.CoreGroupData;
import vn.tr.core.data.validator.CoreGroupValidator;

import java.util.List;

/**
 * Controller quản lý các nghiệp vụ cho CoreGroup.
 * Đường dẫn API trong service này là tương đối, prefix chung được quản lý bởi API Gateway.
 * Mọi thao tác đều yêu cầu ngữ cảnh ứng dụng hợp lệ.
 *
 * @author tyran8x
 * @version 2.2 (Final with Javadoc)
 */
@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class CoreGroupController {
	
	private final CoreGroupBusiness coreGroupBusiness;
	private final CoreGroupValidator coreGroupValidator;
	
	@InitBinder("coreGroupData")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(coreGroupValidator);
	}
	
	/**
	 * Tạo mới một nhóm.
	 * Ngữ cảnh ứng dụng được lấy từ token của người dùng đăng nhập.
	 *
	 * @param coreGroupData Dữ liệu của nhóm cần tạo.
	 *
	 * @return Dữ liệu của nhóm sau khi tạo thành công.
	 */
	@PostMapping
	@Log(title = "Tạo mới Nhóm", businessType = BusinessType.INSERT)
	public R<CoreGroupData> create(@Valid @RequestBody CoreGroupData coreGroupData) {
		String appCodeContext = LoginHelper.getAppCode();
		CoreGroupData createdGroup = coreGroupBusiness.create(coreGroupData, appCodeContext);
		return R.ok(createdGroup);
	}
	
	/**
	 * Cập nhật thông tin một nhóm.
	 *
	 * @param id            ID của nhóm cần cập nhật.
	 * @param coreGroupData Dữ liệu mới của nhóm.
	 *
	 * @return Dữ liệu của nhóm sau khi cập nhật.
	 */
	@PutMapping("/{id}")
	@Log(title = "Cập nhật Nhóm", businessType = BusinessType.UPDATE)
	public R<CoreGroupData> update(@PathVariable Long id, @Valid @RequestBody CoreGroupData coreGroupData) {
		String appCodeContext = LoginHelper.getAppCode();
		CoreGroupData updatedGroup = coreGroupBusiness.update(id, coreGroupData, appCodeContext);
		return R.ok(updatedGroup);
	}
	
	/**
	 * Xóa một nhóm duy nhất (xóa mềm).
	 *
	 * @param id ID của nhóm cần xóa.
	 *
	 * @return R.ok() nếu thành công, hoặc lỗi 4xx/5xx nếu thất bại.
	 */
	@DeleteMapping("/{id}")
	@Log(title = "Xóa Nhóm", businessType = BusinessType.DELETE)
	public R<Void> delete(@PathVariable Long id) {
		String appCodeContext = LoginHelper.getAppCode();
		coreGroupBusiness.delete(id, appCodeContext);
		return R.ok();
	}
	
	/**
	 * Xóa hàng loạt nhóm (xóa mềm).
	 * API này sẽ trả về một báo cáo chi tiết về kết quả xóa của từng ID.
	 *
	 * @param deleteData Đối tượng chứa danh sách các ID cần xóa.
	 *
	 * @return Một đối tượng BulkOperationResult chứa danh sách thành công và thất bại.
	 */
	@DeleteMapping
	@Log(title = "Xóa hàng loạt Nhóm", businessType = BusinessType.DELETE)
	public R<BulkOperationResult<Long>> bulkDelete(@Valid @RequestBody DeleteData deleteData) {
		String appCodeContext = LoginHelper.getAppCode();
		BulkOperationResult<Long> result = coreGroupBusiness.bulkDelete(deleteData.getIds(), appCodeContext);
		return R.ok(result);
	}
	
	/**
	 * Lấy thông tin chi tiết của một nhóm.
	 *
	 * @param id ID của nhóm cần tìm.
	 *
	 * @return Dữ liệu chi tiết của nhóm.
	 */
	@GetMapping("/{id}")
	@Log(title = "Lấy chi tiết Nhóm", businessType = BusinessType.DETAIL, isSaveRequestData = false)
	public R<CoreGroupData> findById(@PathVariable long id) {
		String appCodeContext = LoginHelper.getAppCode();
		CoreGroupData coreGroupData = coreGroupBusiness.findById(id, appCodeContext);
		return R.ok(coreGroupData);
	}
	
	/**
	 * Tìm kiếm và trả về danh sách nhóm có phân trang.
	 *
	 * @param criteria Các tiêu chí để tìm kiếm.
	 *
	 * @return Kết quả phân trang của danh sách nhóm.
	 */
	@GetMapping
	@Log(title = "Tìm kiếm Nhóm (phân trang)", businessType = BusinessType.FINDALL, isSaveRequestData = false)
	public R<PagedResult<CoreGroupData>> findAll(CoreGroupSearchCriteria criteria) {
		String appCodeContext = LoginHelper.getAppCode();
		PagedResult<CoreGroupData> pagedResult = coreGroupBusiness.findAll(criteria, appCodeContext);
		return R.ok(pagedResult);
	}
	
	/**
	 * Lấy toàn bộ danh sách nhóm (không phân trang), thường dùng cho các dropdown.
	 *
	 * @param criteria Các tiêu chí để lọc.
	 *
	 * @return Danh sách đầy đủ các nhóm.
	 */
	@GetMapping("/list")
	@Log(title = "Lấy danh sách Nhóm", businessType = BusinessType.FINDALL, isSaveRequestData = false)
	public R<List<CoreGroupData>> getAll(CoreGroupSearchCriteria criteria) {
		String appCodeContext = LoginHelper.getAppCode();
		List<CoreGroupData> groupList = coreGroupBusiness.getAll(criteria, appCodeContext);
		return R.ok(groupList);
	}
}
