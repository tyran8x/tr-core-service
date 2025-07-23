package vn.tr.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.common.core.domain.model.LoginUser;
import vn.tr.common.log.annotation.Log;
import vn.tr.common.log.enums.BusinessType;
import vn.tr.common.satoken.annotation.AppCode;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.common.web.utils.PagedResult;
import vn.tr.core.business.CoreUserBusiness;
import vn.tr.core.data.criteria.CoreUserSearchCriteria;
import vn.tr.core.data.dto.CoreUserChangePasswordData;
import vn.tr.core.data.dto.CoreUserChangeStatusData;
import vn.tr.core.data.dto.CoreUserData;
import vn.tr.core.data.validator.CoreUserValidator;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class CoreUserController {
	
	private final CoreUserBusiness coreUserBusiness;
	private final CoreUserValidator coreUserValidator;
	
	@InitBinder("coreUserData")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(coreUserValidator);
	}
	
	@PostMapping
	@Log(title = "Tạo mới Người dùng", businessType = BusinessType.INSERT)
	public R<CoreUserData> create(@Valid @RequestBody CoreUserData coreUserData, @AppCode String appCode) {
		CoreUserData createdUser = coreUserBusiness.create(coreUserData, appCode);
		return R.ok(createdUser);
	}
	
	@PutMapping("/{id}")
	@Log(title = "Cập nhật Người dùng", businessType = BusinessType.UPDATE)
	public R<CoreUserData> update(@PathVariable Long id, @Valid @RequestBody CoreUserData coreUserData, @AppCode String appCode) {
		CoreUserData updatedUser = coreUserBusiness.update(id, coreUserData, appCode);
		return R.ok(updatedUser);
	}
	
	@DeleteMapping("/{id}")
	@Log(title = "Xóa Người dùng", businessType = BusinessType.DELETE)
	public R<Void> delete(@PathVariable Long id, @AppCode String appCode) {
		coreUserBusiness.delete(id, appCode);
		return R.ok("Xóa người dùng thành công");
	}
	
	@DeleteMapping
	@Log(title = "Xóa hàng loạt Người dùng", businessType = BusinessType.DELETE)
	public R<Void> bulkDelete(@Valid @RequestBody Set<Long> ids, @AppCode String appCode) {
		if (ids == null || ids.isEmpty()) {
			return R.fail("Danh sách ID không được để trống.");
		}
		coreUserBusiness.bulkDelete(ids, appCode);
		return R.ok("Xóa hàng loạt người dùng thành công");
	}
	
	@GetMapping
	@Log(title = "Tìm kiếm Người dùng (Phân trang)", businessType = BusinessType.FINDALL, isSaveRequestData = false)
	public R<PagedResult<CoreUserData>> findAll(CoreUserSearchCriteria criteria, @AppCode String appCode) {
		PagedResult<CoreUserData> pageCoreUserData = coreUserBusiness.findAll(criteria, appCode);
		return R.ok(pageCoreUserData);
	}
	
	@GetMapping("/list")
	@Log(title = "Lấy danh sách Người dùng", businessType = BusinessType.FINDALL, isSaveRequestData = false)
	public R<List<CoreUserData>> getAll(CoreUserSearchCriteria criteria, @AppCode String appCode) {
		List<CoreUserData> coreUserDatas = coreUserBusiness.getAll(criteria, appCode);
		return R.ok(coreUserDatas);
	}
	
	@GetMapping("/{id}")
	@Log(title = "Lấy chi tiết Người dùng", businessType = BusinessType.DETAIL, isSaveRequestData = false)
	public R<CoreUserData> findById(@PathVariable long id, @AppCode String appCode) {
		CoreUserData coreUserData = coreUserBusiness.findById(id, appCode);
		return R.ok(coreUserData);
	}
	
	@PatchMapping("/{username}/change-password")
	@Log(title = "Đổi mật khẩu Người dùng", businessType = BusinessType.UPDATE)
	public R<Void> changePassword(@PathVariable String username, @Valid @RequestBody CoreUserChangePasswordData request, @AppCode String appCode) {
		coreUserBusiness.changePassword(username, request.getPassword(), appCode);
		return R.ok("Đổi mật khẩu thành công");
	}
	
	@PatchMapping("/{username}/update-status")
	@Log(title = "Cập nhật trạng thái Người dùng", businessType = BusinessType.UPDATE)
	public R<Void> updateStatus(@PathVariable String username, @Valid @RequestBody CoreUserChangeStatusData request, @AppCode String appCode) {
		coreUserBusiness.updateStatus(username, request.getNewStatus(), appCode);
		return R.ok("Cập nhật trạng thái thành công");
	}
	
	@GetMapping("/me")
	public R<CoreUserData> getCurrentUser() {
		LoginUser loginUser = LoginHelper.getLoginUserOrThrow();
		CoreUserData coreUserData = coreUserBusiness.findById(loginUser.getUserId(), loginUser.getAppCode());
		return R.ok(coreUserData);
	}
}

