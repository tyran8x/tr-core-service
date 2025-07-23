package vn.tr.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.common.core.domain.model.LoginUser;
import vn.tr.common.core.exception.base.InvalidEntityException;
import vn.tr.common.log.annotation.Log;
import vn.tr.common.log.enums.BusinessType;
import vn.tr.common.satoken.annotation.AppCode;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.common.web.data.dto.BulkOperationResult;
import vn.tr.common.web.data.dto.DeleteData;
import vn.tr.common.web.utils.PagedResult;
import vn.tr.core.business.CoreUserBusiness;
import vn.tr.core.data.criteria.CoreUserSearchCriteria;
import vn.tr.core.data.dto.CoreUserChangePasswordData;
import vn.tr.core.data.dto.CoreUserChangeStatusData;
import vn.tr.core.data.dto.CoreUserData;
import vn.tr.core.data.validator.CoreUserValidator;

import java.util.List;

/**
 * Controller quản lý toàn bộ các nghiệp vụ liên quan đến Người dùng (CoreUser). Tận dụng @AppCode Argument Resolver để tự động hóa việc xác định ngữ
 * cảnh ứng dụng.
 *
 * @author tyran8x
 * @version 3.0
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class CoreUserController {
	
	private final CoreUserBusiness coreUserBusiness;
	private final CoreUserValidator coreUserValidator;
	
	@InitBinder("coreUserData")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(coreUserValidator);
	}
	
	/**
	 * Tạo mới một người dùng.
	 *
	 * @param coreUserData Dữ liệu người dùng.
	 * @param appCode      Ngữ cảnh ứng dụng, được resolver điền. Super Admin có thể chỉ định qua body.
	 *
	 * @return Dữ liệu người dùng sau khi tạo.
	 */
	@PostMapping
	@Log(title = "Tạo mới Người dùng", businessType = BusinessType.INSERT)
	public R<CoreUserData> create(@Valid @RequestBody CoreUserData coreUserData, @AppCode String appCode) {
		if (LoginHelper.isSuperAdmin() && (appCode == null || appCode.isBlank())) {
			throw new InvalidEntityException("Super Admin phải cung cấp 'appCode' trong request body khi tạo người dùng.");
		}
		CoreUserData createdUser = coreUserBusiness.create(coreUserData, appCode);
		return R.ok(createdUser);
	}
	
	/**
	 * Cập nhật thông tin người dùng.
	 *
	 * @param id           ID của người dùng.
	 * @param coreUserData Dữ liệu mới.
	 * @param appCode      Ngữ cảnh ứng dụng.
	 *
	 * @return Dữ liệu người dùng sau khi cập nhật.
	 */
	@PutMapping("/{id}")
	@Log(title = "Cập nhật Người dùng", businessType = BusinessType.UPDATE)
	public R<CoreUserData> update(@PathVariable Long id, @Valid @RequestBody CoreUserData coreUserData, @AppCode String appCode) {
		if (LoginHelper.isSuperAdmin() && (appCode == null || appCode.isBlank())) {
			throw new InvalidEntityException("Super Admin phải cung cấp 'appCode' trong request body khi cập nhật người dùng.");
		}
		CoreUserData updatedUser = coreUserBusiness.update(id, coreUserData, appCode);
		return R.ok(updatedUser);
	}
	
	/**
	 * Xóa một người dùng (xóa mềm).
	 *
	 * @param id      ID của người dùng cần xóa.
	 * @param appCode Ngữ cảnh ứng dụng.
	 *
	 * @return R.ok()
	 */
	@DeleteMapping("/{id}")
	@Log(title = "Xóa Người dùng", businessType = BusinessType.DELETE)
	public R<Void> delete(@PathVariable Long id, @AppCode String appCode) {
		if (LoginHelper.isSuperAdmin() && (appCode == null || appCode.isBlank())) {
			throw new InvalidEntityException("Super Admin phải chỉ định 'appCode' qua query parameter khi xóa người dùng.");
		}
		coreUserBusiness.delete(id, appCode);
		return R.ok();
	}
	
	/**
	 * Xóa hàng loạt người dùng (xóa mềm) và trả về báo cáo chi tiết.
	 *
	 * @param deleteData DTO chứa danh sách ID cần xóa.
	 * @param appCode    Ngữ cảnh ứng dụng.
	 *
	 * @return Báo cáo chi tiết kết quả xóa.
	 */
	@DeleteMapping
	@Log(title = "Xóa hàng loạt Người dùng", businessType = BusinessType.DELETE)
	public R<BulkOperationResult<Long>> bulkDelete(@Valid @RequestBody DeleteData deleteData, @AppCode String appCode) {
		if (LoginHelper.isSuperAdmin() && (appCode == null || appCode.isBlank())) {
			throw new InvalidEntityException("Super Admin phải chỉ định 'appCode' qua query parameter khi xóa hàng loạt.");
		}
		BulkOperationResult<Long> result = coreUserBusiness.bulkDelete(deleteData.getIds(), appCode);
		return R.ok(result);
	}
	
	/**
	 * Lấy thông tin chi tiết của một người dùng.
	 *
	 * @param id      ID của người dùng.
	 * @param appCode Ngữ cảnh ứng dụng. App Admin chỉ có thể xem user trong app của mình.
	 *
	 * @return Dữ liệu chi tiết của người dùng.
	 */
	@GetMapping("/{id}")
	@Log(title = "Lấy chi tiết Người dùng", businessType = BusinessType.DETAIL)
	public R<CoreUserData> findById(@PathVariable long id, @AppCode String appCode) {
		// Đối với App Admin, appCode luôn có giá trị.
		// Đối với Super Admin, appCode có thể là null, findById trong business sẽ trả về toàn bộ thông tin.
		CoreUserData coreUserData = coreUserBusiness.findById(id, appCode);
		return R.ok(coreUserData);
	}
	
	/**
	 * Tìm kiếm và phân trang người dùng.
	 *
	 * @param criteria Tiêu chí tìm kiếm.
	 * @param appCode  Ngữ cảnh ứng dụng.
	 *
	 * @return Kết quả phân trang.
	 */
	@GetMapping
	@Log(title = "Tìm kiếm Người dùng (Phân trang)", businessType = BusinessType.FINDALL)
	public R<PagedResult<CoreUserData>> findAll(CoreUserSearchCriteria criteria, @AppCode String appCode) {
		PagedResult<CoreUserData> pageResult = coreUserBusiness.findAll(criteria, appCode);
		return R.ok(pageResult);
	}
	
	/**
	 * Lấy danh sách đầy đủ người dùng (không phân trang).
	 *
	 * @param criteria Tiêu chí tìm kiếm.
	 * @param appCode  Ngữ cảnh ứng dụng.
	 *
	 * @return Danh sách người dùng.
	 */
	@GetMapping("/list")
	@Log(title = "Lấy danh sách Người dùng", businessType = BusinessType.FINDALL)
	public R<List<CoreUserData>> getAll(CoreUserSearchCriteria criteria, @AppCode String appCode) {
		List<CoreUserData> coreUserDatas = coreUserBusiness.getAll(criteria, appCode);
		return R.ok(coreUserDatas);
	}
	
	/**
	 * Đổi mật khẩu cho một người dùng.
	 *
	 * @param username Username của người dùng.
	 * @param request  DTO chứa mật khẩu mới.
	 * @param appCode  Ngữ cảnh ứng dụng.
	 *
	 * @return R.ok()
	 */
	@PatchMapping("/{username}/change-password")
	@Log(title = "Đổi mật khẩu Người dùng", businessType = BusinessType.UPDATE)
	public R<Void> changePassword(@PathVariable String username, @Valid @RequestBody CoreUserChangePasswordData request, @AppCode String appCode) {
		if (LoginHelper.isSuperAdmin() && (appCode == null || appCode.isBlank())) {
			throw new InvalidEntityException("Super Admin phải chỉ định 'appCode' qua query parameter khi đổi mật khẩu.");
		}
		coreUserBusiness.changePassword(username, request.getPassword(), appCode);
		return R.ok();
	}
	
	/**
	 * Cập nhật trạng thái của một người dùng.
	 *
	 * @param username Username của người dùng.
	 * @param request  DTO chứa trạng thái mới.
	 * @param appCode  Ngữ cảnh ứng dụng.
	 *
	 * @return R.ok()
	 */
	@PatchMapping("/{username}/update-status")
	@Log(title = "Cập nhật trạng thái Người dùng", businessType = BusinessType.UPDATE)
	public R<Void> updateStatus(@PathVariable String username, @Valid @RequestBody CoreUserChangeStatusData request, @AppCode String appCode) {
		if (LoginHelper.isSuperAdmin() && (appCode == null || appCode.isBlank())) {
			throw new InvalidEntityException("Super Admin phải chỉ định 'appCode' qua query parameter khi cập nhật trạng thái.");
		}
		coreUserBusiness.updateStatus(username, request.getNewStatus(), appCode);
		return R.ok();
	}
	
	/**
	 * Lấy thông tin của chính người dùng đang đăng nhập.
	 *
	 * @return Dữ liệu chi tiết của người dùng hiện tại.
	 */
	@GetMapping("/me")
	@Log(title = "Lấy thông tin cá nhân", businessType = BusinessType.DETAIL)
	public R<CoreUserData> getCurrentUser() {
		LoginUser loginUser = LoginHelper.getLoginUserOrThrow();
		// Khi lấy thông tin cá nhân, ngữ cảnh appCode chính là appCode trong token (nếu có)
		// Nếu là Super Admin, appCode là null, sẽ lấy toàn bộ thông tin
		CoreUserData coreUserData = coreUserBusiness.findById(loginUser.getUserId(), loginUser.getAppCode());
		return R.ok(coreUserData);
	}
}
