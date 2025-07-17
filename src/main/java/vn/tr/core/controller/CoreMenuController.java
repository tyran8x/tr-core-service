package vn.tr.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.common.log.annotation.Log;
import vn.tr.common.log.enums.BusinessType;
import vn.tr.common.web.data.dto.DeleteData;
import vn.tr.core.business.CoreMenuBusiness;
import vn.tr.core.data.CoreDsMenuData;
import vn.tr.core.data.RouteRecordRawData;
import vn.tr.core.data.criteria.CoreMenuSearchCriteria;
import vn.tr.core.data.dto.CoreMenuData;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/menus")
@RequiredArgsConstructor
public class CoreMenuController {
	
	private final CoreMenuBusiness coreMenuBusiness;
	
	@PostMapping(value = {""})
	@Log(title = "create CoreMenu", businessType = BusinessType.DELETE)
	public R<CoreMenuData> create(@Valid @RequestBody CoreMenuData coreMenuData) {
		coreMenuData = coreMenuBusiness.create(coreMenuData);
		return R.ok(coreMenuData);
	}
	
	@DeleteMapping(value = {"/{id}"})
	@Log(title = "Delete CoreMenu", businessType = BusinessType.DELETE)
	public R<Void> delete(@PathVariable("id") Long id) {
		coreMenuBusiness.delete(id);
		return R.ok();
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
	public R<Page<CoreMenuData>> findAll(CoreMenuSearchCriteria criteria) {
		Page<CoreMenuData> pageCoreMenuData = coreMenuBusiness.findAll(criteria);
		return R.ok(pageCoreMenuData);
	}
	
	@GetMapping(value = {"/list"})
	@Log(title = "getAll CoreMenu", businessType = BusinessType.FINDALL, isSaveRequestData = false)
	public R<List<CoreMenuData>> getAll(CoreMenuSearchCriteria criteria) {
		List<CoreMenuData> coreMenuDatas = coreMenuBusiness.getAll(criteria);
		return R.ok(coreMenuDatas);
	}
	
	@GetMapping(value = {"/{id}"})
	@Log(title = "findById CoreMenu", businessType = BusinessType.DETAIL, isSaveRequestData = false)
	public R<CoreMenuData> findById(@PathVariable("id") long id) {
		CoreMenuData coreMenuData = coreMenuBusiness.findById(id);
		return R.ok(coreMenuData);
	}
	
	@GetMapping(value = {"/get/{id}"})
	public R<CoreMenuData> getById(@PathVariable("id") Long id) {
		Optional<CoreMenuData> optionalCoreMenuData = coreMenuBusiness.getById(id);
		return R.ok(optionalCoreMenuData.orElse(null));
	}
	
	@PutMapping(value = {"/{id}"})
	@Log(title = "update CoreMenu", businessType = BusinessType.UPDATE)
	public R<CoreMenuData> update(@PathVariable("id") Long id, @Valid @RequestBody CoreMenuData coreMenuData) {
		coreMenuData = coreMenuBusiness.update(id, coreMenuData);
		return R.ok(coreMenuData);
	}
	
	@GetMapping(value = "/getRouters")
	public R<CoreDsMenuData> getRouters(@RequestHeader(name = "X-App-Code", required = false) String xAppCode) {
		CoreDsMenuData coreDsMenuData = coreMenuBusiness.getRouterDatas(xAppCode);
		return R.ok(coreDsMenuData);
	}
	
	@PostMapping(value = {"/setRouters"})
	public R<String> setRouters(@RequestHeader(name = "X-App-Code", required = false) String xAppCode, @Valid @RequestBody Object object) {
		String thongBao = "Đang thực hiện get dữ liệu router, vui lòng chờ !";
		
		coreMenuBusiness.setRouterDatas(object, xAppCode);
//		while (future.isDone()) {
//			thongBao = "Đã hoàn thành get dữ liệu router";
//		}
		return R.ok(thongBao);
	}
	
	@GetMapping(value = "/getRoutes")
	public R<List<RouteRecordRawData>> getRoutes(@RequestHeader(name = "X-App-Code", required = false) String xAppCode) {
		List<RouteRecordRawData> routeRecordRawDatas = coreMenuBusiness.getRoutes(xAppCode);
		return R.ok(routeRecordRawDatas);
	}
	
	@PostMapping(value = {"/setRoutes"})
	public R<String> setRoutes(@RequestHeader(name = "X-App-Code", required = false) String xAppCode, @Valid @RequestBody Object object) {
		String thongBao = "Đang thực hiện get dữ liệu router, vui lòng chờ !";
		
		coreMenuBusiness.setRoutes(object, xAppCode);
//		while (future.isDone()) {
//			thongBao = "Đã hoàn thành get dữ liệu router";
//		}
		return R.ok(thongBao);
	}
	
}
