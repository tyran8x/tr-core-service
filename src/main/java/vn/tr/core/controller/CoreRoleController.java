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
import vn.tr.core.business.CoreRoleBusiness;
import vn.tr.core.data.criteria.CoreRoleSearchCriteria;
import vn.tr.core.data.dto.CoreRoleData;
import vn.tr.core.data.validator.CoreRoleValidator;

import java.util.Set;

/**
 * Controller quản lý các nghiệp vụ cho CoreRole.
 * Mọi thao tác đều yêu cầu ngữ cảnh ứng dụng hợp lệ.
 *
 * @author tyran8x
 * @version 2.1
 */
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class CoreRoleController {
	
	private final CoreRoleBusiness coreRoleBusiness;
	private final CoreRoleValidator coreRoleValidator;
	
	@InitBinder("coreRoleData")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(coreRoleValidator);
	}
	
	/**
	 * Tạo mới một vai trò.
	 *
	 * @param coreRoleData Dữ liệu của vai trò cần tạo.
	 *
	 * @return Dữ liệu của vai trò sau khi tạo thành công.
	 */
	@PostMapping
	@Log(title = "Tạo mới Vai trò", businessType = BusinessType.INSERT)
	public R<CoreRoleData> create(@Valid @RequestBody CoreRoleData coreRoleData) {
		String appCodeContext = LoginHelper.getAppCode();
		return R.ok(coreRoleBusiness.create(coreRoleData, appCodeContext));
	}
	
	/**
	 * Cập nhật thông tin một vai trò.
	 *
	 * @param id           ID của vai trò cần cập nhật.
	 * @param coreRoleData Dữ liệu mới của vai trò.
	 *
	 * @return Dữ liệu của vai trò sau khi cập nhật.
	 */
	@PutMapping("/{id}")
	@Log(title = "Cập nhật Vai trò", businessType = BusinessType.UPDATE)
	public R<CoreRoleData> update(@PathVariable Long id, @Valid @RequestBody CoreRoleData coreRoleData) {
		String appCodeContext = LoginHelper.getAppCode();
		return R.ok(coreRoleBusiness.update(id, coreRoleData, appCodeContext));
	}
	
	/**
	 * Xóa một vai trò duy nhất (xóa mềm).
	 *
	 * @param id ID của vai trò cần xóa.
	 *
	 * @return R.ok() nếu thành công.
	 */
	@DeleteMapping("/{id}")
	@Log(title = "Xóa Vai trò", businessType = BusinessType.DELETE)
	public R<Void> delete(@PathVariable Long id) {
		String appCodeContext = LoginHelper.getAppCode();
		coreRoleBusiness.delete(id, appCodeContext);
		return R.ok();
	}
	
	/**
	 * Xóa hàng loạt vai trò (xóa mềm).
	 *
	 * @param deleteData Đối tượng chứa danh sách các ID cần xóa.
	 *
	 * @return Một báo cáo chi tiết về kết quả xóa của từng ID.
	 */
	@DeleteMapping
	@Log(title = "Xóa hàng loạt Vai trò", businessType = BusinessType.DELETE)
	public R<BulkOperationResult<Long>> bulkDelete(@Valid @RequestBody DeleteData deleteData) {
		String appCodeContext = LoginHelper.getAppCode();
		return R.ok(coreRoleBusiness.bulkDelete(deleteData.getIds(), appCodeContext));
	}
	
	/**
	 * Lấy thông tin chi tiết của một vai trò.
	 *
	 * @param id ID của vai trò.
	 *
	 * @return Dữ liệu chi tiết của vai trò.
	 */
	@GetMapping("/{id}")
	@Log(title = "Lấy chi tiết Vai trò", businessType = BusinessType.DETAIL)
	public R<CoreRoleData> findById(@PathVariable long id) {
		String appCodeContext = LoginHelper.getAppCode();
		return R.ok(coreRoleBusiness.findById(id, appCodeContext));
	}
	
	/**
	 * Tìm kiếm và trả về danh sách vai trò có phân trang.
	 *
	 * @param criteria Các tiêu chí tìm kiếm.
	 *
	 * @return Kết quả phân trang của danh sách vai trò.
	 */
	@GetMapping
	@Log(title = "Tìm kiếm Vai trò (phân trang)", businessType = BusinessType.FINDALL)
	public R<PagedResult<CoreRoleData>> findAll(CoreRoleSearchCriteria criteria) {
		String appCodeContext = LoginHelper.getAppCode();
		return R.ok(coreRoleBusiness.findAll(criteria, appCodeContext));
	}
	
	/**
	 * Đồng bộ hóa (thay thế hoàn toàn) danh sách quyền hạn cho một vai trò.
	 *
	 * @param roleId          ID của vai trò.
	 * @param permissionCodes Danh sách các mã quyền hạn mới.
	 *
	 * @return Dữ liệu của vai trò sau khi cập nhật quyền hạn.
	 */
	@PutMapping("/{roleId}/permissions")
	@Log(title = "Đồng bộ Quyền hạn cho Vai trò", businessType = BusinessType.UPDATE)
	public R<CoreRoleData> synchronizePermissions(@PathVariable Long roleId, @RequestBody Set<String> permissionCodes) {
		String appCodeContext = LoginHelper.getAppCode();
		CoreRoleData updatedRole = coreRoleBusiness.synchronizePermissionsForRole(roleId, permissionCodes, appCodeContext);
		return R.ok(updatedRole);
	}
}
