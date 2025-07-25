package vn.tr.core.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.common.log.annotation.Log;
import vn.tr.common.log.enums.BusinessType;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.common.web.data.dto.DeleteData;
import vn.tr.common.web.utils.PagedResult;
import vn.tr.core.business.CoreMenuBusiness;
import vn.tr.core.dao.service.CoreSyncService;
import vn.tr.core.data.criteria.CoreMenuSearchCriteria;
import vn.tr.core.data.dto.CoreMenuData;
import vn.tr.core.data.dto.RouteRecordRawData;
import vn.tr.core.data.validator.CoreMenuValidator;

import java.util.List;

@RestController
@RequestMapping(value = "/menus")
@RequiredArgsConstructor
@Slf4j
public class CoreMenuController {
	
	private final CoreMenuBusiness coreMenuBusiness;
	private final CoreMenuValidator coreMenuValidator;
	private final CoreSyncService coreSyncService;
	
	@InitBinder("coreMenuData")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(coreMenuValidator);
	}
	
	@SaCheckRole("CoreMenuList")
	@SaCheckPermission("CoreMenuList")
	@GetMapping("/tree")
	@Log(title = "Lấy cây Menu", businessType = BusinessType.LIST, isSaveRequestData = false)
	public R<List<CoreMenuData>> getMenuTree() {
		List<CoreMenuData> menuTree = coreMenuBusiness.getMenuTreeForApp(LoginHelper.getAppCode());
		return R.ok(menuTree);
	}
	
	@GetMapping("/flat-list")
	@Log(title = "Lấy danh sách phẳng Menu", businessType = BusinessType.LIST, isSaveRequestData = false)
	public R<List<CoreMenuData>> getFlatList(@RequestHeader(name = "X-App-Code") String appCode) {
		List<CoreMenuData> menuList = coreMenuBusiness.getFlatListForApp(appCode);
		return R.ok(menuList);
	}
	
	@GetMapping("/{id}")
	@Log(title = "Lấy chi tiết Menu", businessType = BusinessType.DETAIL, isSaveRequestData = false)
	public R<CoreMenuData> getById(@PathVariable Long id) {
		CoreMenuData menuData = coreMenuBusiness.findById(id);
		return R.ok(menuData);
	}
	
	@DeleteMapping(value = "")
	@Log(title = "bulkDelete CoreMenu", businessType = BusinessType.DELETE)
	public R<Void> bulkDelete(@Valid @RequestBody DeleteData deleteData) {
		if (deleteData.getIds() == null || deleteData.getIds().isEmpty()) {
			return R.fail("Danh sách ID không được để trống.");
		}
		coreMenuBusiness.bulkDelete(deleteData.getIds());
		return R.ok();
	}
	
	@GetMapping(value = {"/", ""})
	@Log(title = "FindAll CoreMenu", businessType = BusinessType.FINDALL, isSaveRequestData = false)
	public R<PagedResult<CoreMenuData>> findAll(CoreMenuSearchCriteria criteria) {
		PagedResult<CoreMenuData> pageCoreMenuData = coreMenuBusiness.findAll(criteria);
		return R.ok(pageCoreMenuData);
	}
	
	@GetMapping(value = {"/list"})
	@Log(title = "getAll CoreMenu", businessType = BusinessType.FINDALL, isSaveRequestData = false)
	public R<List<CoreMenuData>> getAll(CoreMenuSearchCriteria criteria) {
		List<CoreMenuData> coreMenuDatas = coreMenuBusiness.getAll(criteria);
		return R.ok(coreMenuDatas);
	}
	
	@PostMapping
	@Log(title = "Tạo Menu", businessType = BusinessType.INSERT)
	public R<CoreMenuData> create(@Valid @RequestBody CoreMenuData menuData) {
		CoreMenuData createdMenu = coreMenuBusiness.create(menuData);
		return R.ok(createdMenu);
	}
	
	@PutMapping("/{id}")
	@Log(title = "Cập nhật Menu", businessType = BusinessType.UPDATE)
	public R<CoreMenuData> update(@PathVariable Long id, @Valid @RequestBody CoreMenuData menuData) {
		CoreMenuData updatedMenu = coreMenuBusiness.update(id, menuData);
		return R.ok(updatedMenu);
	}
	
	@DeleteMapping("/{id}")
	@Log(title = "Xóa Menu", businessType = BusinessType.DELETE)
	public R<Void> delete(@PathVariable Long id) {
		coreMenuBusiness.delete(id);
		return R.ok("Xóa menu thành công.");
	}
	
	@GetMapping("/routes")
	public R<List<RouteRecordRawData>> getAccessibleRoutes(@RequestHeader(name = "X-App-Code") String appCode) {
		List<RouteRecordRawData> accessibleRoutes = coreMenuBusiness.getAccessibleRoutesForCurrentUser(appCode);
		return R.ok(accessibleRoutes);
	}
	
	@PostMapping("/routes")
	@Log(title = "Đồng bộ hóa Routes từ Frontend", businessType = BusinessType.UPDATE)
	// Cần một quyền đặc biệt để bảo vệ endpoint này, ví dụ: 'SystemSyncRoutes'
	// @PreAuthorize("hasAuthority('SystemSyncRoutes')")
	public R<Void> syncRoutes(@RequestHeader(name = "X-App-Code") String appCode, @RequestBody Object object) {
		log.info("APP-CODE: {}", appCode);
		try {
			coreSyncService.syncRoutesFromFrontend(appCode, object);
			return R.ok("Đồng bộ hóa routes thành công.");
		} catch (Exception e) {
			// Ghi log lỗi chi tiết
			log.error("Lỗi khi đồng bộ hóa routes cho app {}: {}", appCode, e.getMessage());
			return R.fail("Đồng bộ hóa routes thất bại. Vui lòng kiểm tra log.");
		}
	}
	
}
