package vn.tr.core.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.core.business.CoreMenuBusiness;
import vn.tr.core.data.CoreDsMenuData;
import vn.tr.core.data.CoreMenuData;

import java.util.List;

@RestController
@RequestMapping(value = "/menu")
public class CoreMenuController {
	
	private final CoreMenuBusiness coreMenuBusiness;
	
	public CoreMenuController(CoreMenuBusiness coreMenuBusiness) {
		this.coreMenuBusiness = coreMenuBusiness;
	}
	
	@PostMapping(value = {""})
	public R<CoreMenuData> create(@Valid @RequestBody CoreMenuData coreMenuData) {
		coreMenuData = coreMenuBusiness.create(coreMenuData);
		return R.ok(coreMenuData);
	}
	
	@DeleteMapping(value = {"/{id}"})
	public R<Void> delete(@PathVariable("id") Long id) throws EntityNotFoundException {
		coreMenuBusiness.delete(id);
		return R.ok();
	}
	
	@DeleteMapping(value = {"/ids"})
	public R<Void> deleteByIds(@RequestParam(name = "ids") List<Long> ids) {
		coreMenuBusiness.deleteByIds(ids);
		return R.ok();
	}
	
	@GetMapping(value = {"/", ""})
	public R<Page<CoreMenuData>> findAll(
			@RequestHeader(name = "X-App-Code", required = false) String xAppCode,
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "20", required = false) int size,
			@RequestParam(name = "sortBy", defaultValue = "ten", required = false) String sortBy,
			@RequestParam(name = "sortDir", defaultValue = "ASC", required = false) String sortDir,
			@RequestParam(name = "search", required = false) String search,
			@RequestParam(name = "trangThai", required = false) Boolean trangThai) {
		Page<CoreMenuData> pageCoreMenuData = coreMenuBusiness.findAll(page, size, sortBy, sortDir, search, trangThai, xAppCode);
		return R.ok(pageCoreMenuData);
	}
	
	@GetMapping(value = "/{id}")
	public R<CoreMenuData> findById(@PathVariable("id") Long id) throws EntityNotFoundException {
		CoreMenuData coreMenuData = coreMenuBusiness.findById(id);
		return R.ok(coreMenuData);
	}
	
	@GetMapping(value = "/get")
	public R<List<CoreMenuData>> getAll(@RequestParam(name = "ids", required = false) List<Long> ids) {
		List<CoreMenuData> coreMenuDatas = coreMenuBusiness.getAll(ids);
		return R.ok(coreMenuDatas);
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
	
	@PutMapping(value = {"/{id}"})
	public R<CoreMenuData> update(@PathVariable("id") Long id, @Valid @RequestBody CoreMenuData coreMenuData) throws EntityNotFoundException {
		coreMenuData = coreMenuBusiness.update(id, coreMenuData);
		return R.ok(coreMenuData);
	}
}
