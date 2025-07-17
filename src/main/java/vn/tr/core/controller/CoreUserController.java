package vn.tr.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.common.log.annotation.Log;
import vn.tr.common.log.enums.BusinessType;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.common.web.data.dto.DeleteData;
import vn.tr.core.business.CoreUserBusiness;
import vn.tr.core.data.CoreUserChangePasswordData;
import vn.tr.core.data.CoreUserChangeStatusData;
import vn.tr.core.data.criteria.CoreUserSearchCriteria;
import vn.tr.core.data.dto.CoreUserData;
import vn.tr.core.data.validator.CoreUserValidator;

@RestController
@RequestMapping(value = "/users") // Đổi thành plural "users" theo chuẩn REST
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
		coreUserBusiness.create(coreUserData);
		return R.ok("Tạo người dùng thành công");
	}
	
	@PutMapping("/{id}")
	@Log(title = "Cập nhật Người dùng", businessType = BusinessType.UPDATE)
	public R<CoreUserData> update(@PathVariable Long id, @Valid @RequestBody CoreUserData coreUserData) {
		coreUserBusiness.update(id, coreUserData);
		return R.ok("Cập nhật người dùng thành công");
	}
	
	@DeleteMapping("/{id}")
	@Log(title = "Xóa Người dùng", businessType = BusinessType.DELETE)
	public R<Void> delete(@PathVariable Long id) {
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
	public R<Page<CoreUserData>> findAll(CoreUserSearchCriteria criteria) {
		Page<CoreUserData> pageCoreUserData = coreUserBusiness.findAll(criteria);
		return R.ok(pageCoreUserData);
	}

//	// Giữ lại endpoint này nếu cần lấy danh sách không phân trang
//	@GetMapping("/list")
//	@Log(title = "Lấy danh sách Người dùng", businessType = BusinessType.FINDALL, isSaveRequestData = false)
//	public R<List<CoreUserData>> getAll(CoreUserSearchCriteria criteria) {
//		// Cần thêm hàm getAll vào CoreUserBusiness nếu cần
//		List<CoreUserData> coreUserDatas = coreUserBusiness.getAll(criteria);
//		return R.ok(coreUserDatas);
//	}
	
	@GetMapping("/{id}")
	@Log(title = "Lấy chi tiết Người dùng", businessType = BusinessType.DETAIL, isSaveRequestData = false)
	public R<CoreUserData> findById(@PathVariable long id) {
		CoreUserData coreUserData = coreUserBusiness.findById(id);
		return R.ok(coreUserData);
	}
	
	// Endpoint để lấy thông tin của chính người dùng đang đăng nhập
	@GetMapping("/me")
	@Log(title = "Lấy thông tin cá nhân", businessType = BusinessType.DETAIL, isSaveRequestData = false)
	public R<CoreUserData> getMyInfo() {
		String username = LoginHelper.getUsername(); // Lấy username từ token
		CoreUserData coreUserData = coreUserBusiness.findByUsername(username);
		return R.ok(coreUserData);
	}
	
	// --- Các Endpoint Hành động (Action) ---
	
	@PatchMapping("/{username}/change-password")
	@Log(title = "Đổi mật khẩu Người dùng", businessType = BusinessType.UPDATE)
	public R<Void> changePassword(@PathVariable String username, @Valid @RequestBody CoreUserChangePasswordData request) {
		// Truyền cả username từ path vào để đảm bảo đúng đối tượng
		coreUserBusiness.changePassword(username, request.getPassword());
		return R.ok("Đổi mật khẩu thành công");
	}
	
	@PatchMapping("/{username}/update-status")
	@Log(title = "Cập nhật trạng thái Người dùng", businessType = BusinessType.UPDATE)
	public R<Void> updateStatus(@PathVariable String username, @Valid @RequestBody CoreUserChangeStatusData request) {
		//	coreUserBusiness.updateStatus(username, request.getNewStatus());
		return R.ok("Cập nhật trạng thái thành công");
	}
}
