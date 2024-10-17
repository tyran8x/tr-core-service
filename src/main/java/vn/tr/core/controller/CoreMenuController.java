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
import java.util.concurrent.Future;

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
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "20", required = false) int size,
			@RequestParam(name = "sortBy", defaultValue = "ten", required = false) String sortBy,
			@RequestParam(name = "sortDir", defaultValue = "ASC", required = false) String sortDir,
			@RequestParam(name = "search", required = false) String search,
			@RequestParam(name = "trangThai", required = false) Boolean trangThai,
			@RequestParam(name = "appCode", required = false) String appCode) {
		Page<CoreMenuData> pageCoreMenuData = coreMenuBusiness.findAll(page, size, sortBy, sortDir, search, trangThai, appCode);
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
	public R<CoreDsMenuData> getRouters(@RequestParam("appCode") String appCode) {
		CoreDsMenuData coreDsMenuData = coreMenuBusiness.getRouterDatas(appCode);
		return R.ok(coreDsMenuData);
	}

	@PostMapping(value = {"/setRouters"})
	public R<String> setRouters(@Valid @RequestBody Object object, @RequestParam("appCode") String appCode) {
		String thongBao = "Đang thực hiện get dữ liệu router, vui lòng chờ !";

		Future<String> future = coreMenuBusiness.setRouterDatas(object, appCode);
		while (future.isDone()) {
			thongBao = "Đã hoàn thành get dữ liệu router";
		}
		return R.ok(thongBao);
	}

	@PutMapping(value = {"/{id}"})
	public R<CoreMenuData> update(@PathVariable("id") Long id, @Valid @RequestBody CoreMenuData coreMenuData) throws EntityNotFoundException {
		coreMenuData = coreMenuBusiness.update(id, coreMenuData);
		return R.ok(coreMenuData);
	}
}
