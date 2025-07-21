package vn.tr.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.common.core.domain.model.LoginUser;
import vn.tr.common.log.annotation.Log;
import vn.tr.common.log.enums.BusinessType;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.common.web.data.dto.DeleteData;
import vn.tr.common.web.utils.PagedResult;
import vn.tr.core.business.CoreUserBusiness;
import vn.tr.core.data.criteria.CoreUserSearchCriteria;
import vn.tr.core.data.dto.CoreUserChangePasswordData;
import vn.tr.core.data.dto.CoreUserChangeStatusData;
import vn.tr.core.data.dto.CoreUserData;
import vn.tr.core.data.validator.CoreUserValidator;

import java.util.List;

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
	public R<CoreUserData> create(@Valid @RequestBody CoreUserData coreUserData) {
		// Truyền ngữ cảnh vào lớp business
		CoreUserData createdUser = coreUserBusiness.create(coreUserData, getAppCodeContext());
		return R.ok(createdUser);
	}
	
	private String getAppCodeContext() {
		LoginHelper.isSuperAdmin();
		LoginUser loginUser = LoginHelper.getLoginUserOrThrow();
		// Giả sử Super Admin có vai trò 'ROLE_SUPER_ADMIN'
		// Bạn có thể thay đổi logic này cho phù hợp
		if (LoginHelper.hasRole("ROLE_SUPER_ADMIN")) {
			return null; // Super Admin có ngữ cảnh null (toàn cục)
		}
		return loginUser.getAppCode(); // App Admin bị giới hạn trong app của họ
	}
	
	@PutMapping("/{id}")
	@Log(title = "Cập nhật Người dùng", businessType = BusinessType.UPDATE)
	public R<CoreUserData> update(@PathVariable Long id, @Valid @RequestBody CoreUserData coreUserData) {
		// Truyền ngữ cảnh vào lớp business
		CoreUserData updatedUser = coreUserBusiness.update(id, coreUserData, getAppCodeContext());
		return R.ok(updatedUser);
	}
	
	@DeleteMapping("/{id}")
	@Log(title = "Xóa Người dùng", businessType = BusinessType.DELETE)
	public R<Void> delete(@PathVariable Long id) {
		// Thao tác xóa thường không cần ngữ cảnh, vì ID là duy nhất toàn cục
		// Tuy nhiên, có thể thêm logic kiểm tra xem App Admin có quyền xóa user này không
		coreUserBusiness.delete(id);
		return R.ok("Xóa người dùng thành công");
	}
	
	@DeleteMapping
	@Log(title = "Xóa hàng loạt Người dùng", businessType = BusinessType.DELETE)
	public R<Void> bulkDelete(@Valid @RequestBody DeleteData deleteData) {
		if (deleteData.getIds() == null || deleteData.getIds().isEmpty()) {
			return R.fail("Danh sách ID không được để trống.");
		}
		coreUserBusiness.bulkDelete(deleteData.getIds());
		return R.ok("Xóa hàng loạt người dùng thành công");
	}
	
	@GetMapping
	@Log(title = "Tìm kiếm Người dùng (Phân trang)", businessType = BusinessType.FINDALL, isSaveRequestData = false)
	public R<PagedResult<CoreUserData>> findAll(CoreUserSearchCriteria criteria) {
		// Truyền ngữ cảnh để business biết phạm vi tìm kiếm
		PagedResult<CoreUserData> pageCoreUserData = coreUserBusiness.findAll(criteria, getAppCodeContext());
		return R.ok(pageCoreUserData);
	}
	
	@GetMapping("/list")
	@Log(title = "Lấy danh sách Người dùng", businessType = BusinessType.FINDALL, isSaveRequestData = false)
	public R<List<CoreUserData>> getAll(CoreUserSearchCriteria criteria) {
		// Lớp business cần có phương thức getAll nhận appCodeContext
		List<CoreUserData> coreUserDatas = coreUserBusiness.getAll(criteria, getAppCodeContext());
		return R.ok(coreUserDatas);
	}
	
	@GetMapping("/{id}")
	@Log(title = "Lấy chi tiết Người dùng", businessType = BusinessType.DETAIL, isSaveRequestData = false)
	public R<CoreUserData> findById(@PathVariable long id) {
		// Truyền ngữ cảnh để business biết cần lấy những thông tin liên quan nào (toàn cục hay theo app)
		CoreUserData coreUserData = coreUserBusiness.findById(id, getAppCodeContext());
		return R.ok(coreUserData);
	}
	
	@GetMapping("/me")
	@Log(title = "Lấy thông tin cá nhân", businessType = BusinessType.DETAIL, isSaveRequestData = false)
	public R<CoreUserData> getMyInfo() {
		LoginUser loginUser = LoginHelper.getLoginUserOrThrow();
		CoreUserData coreUserData = coreUserBusiness.findById(loginUser.getUserId(), loginUser.getAppCode());
		return R.ok(coreUserData);
	}
	
	// Các phương thức thay đổi mật khẩu và trạng thái thường không bị ảnh hưởng bởi ngữ cảnh app
	// vì chúng tác động trực tiếp lên đối tượng CoreUser.
	@PatchMapping("/{username}/change-password")
	@Log(title = "Đổi mật khẩu Người dùng", businessType = BusinessType.UPDATE)
	public R<Void> changePassword(@PathVariable String username, @Valid @RequestBody CoreUserChangePasswordData request) {
		coreUserBusiness.changePassword(username, request.getPassword());
		return R.ok("Đổi mật khẩu thành công");
	}
	
	@PatchMapping("/{username}/update-status")
	@Log(title = "Cập nhật trạng thái Người dùng", businessType = BusinessType.UPDATE)
	public R<Void> updateStatus(@PathVariable String username, @Valid @RequestBody CoreUserChangeStatusData request) {
		coreUserBusiness.updateStatus(username, request.getNewStatus());
		return R.ok("Cập nhật trạng thái thành công");
	}
}
