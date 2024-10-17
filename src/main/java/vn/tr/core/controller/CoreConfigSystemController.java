package vn.tr.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.core.business.CoreConfigSystemBusiness;
import vn.tr.core.data.CoreConfigSystemData;

import java.util.List;

@RestController
@RequestMapping(value = "/config/system")
@RequiredArgsConstructor
public class CoreConfigSystemController {

	private final CoreConfigSystemBusiness coreConfigSystemBusiness;

	@PostMapping(value = {""})
	public R<CoreConfigSystemData> create(@Valid @RequestBody CoreConfigSystemData coreConfigSystemData) {
		coreConfigSystemData = coreConfigSystemBusiness.create(coreConfigSystemData);
		return R.ok(coreConfigSystemData);
	}

	@DeleteMapping(value = {"/{id}"})
	public R<Void> delete(@PathVariable("id") Long id) {
		coreConfigSystemBusiness.delete(id);
		return R.ok();
	}

	@DeleteMapping(value = {"/ids"})
	public R<Void> deleteByIds(@RequestParam(name = "ids") List<Long> ids) {
		coreConfigSystemBusiness.deleteByIds(ids);
		return R.ok();
	}

	@GetMapping(value = {"/", ""})
	public R<Page<CoreConfigSystemData>> findAll(
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "20", required = false) int size,
			@RequestParam(name = "sortBy", defaultValue = "ngayCapNhat", required = false) String sortBy,
			@RequestParam(name = "sortDir", defaultValue = "ASC", required = false) String sortDir,
			@RequestParam(name = "maUngDung", required = false) String maUngDung,
			@RequestParam(name = "code", required = false) String code,
			@RequestParam(name = "trangThai", required = false) Boolean trangThai) {
		Page<CoreConfigSystemData> pageCoreConfigSystemData = coreConfigSystemBusiness.findAll(page, size, sortBy, sortDir, maUngDung, code,
				trangThai);
		return R.ok(pageCoreConfigSystemData);
	}

	@GetMapping(value = "/{id}")
	public R<CoreConfigSystemData> findById(@PathVariable("id") Long id) {
		CoreConfigSystemData coreConfigSystemData = coreConfigSystemBusiness.findById(id);
		return R.ok(coreConfigSystemData);
	}

	@GetMapping(value = "/get")
	public R<List<CoreConfigSystemData>> getAll(@RequestParam(name = "ids", required = false) List<Long> ids) {
		List<CoreConfigSystemData> coreConfigSystemDatas = coreConfigSystemBusiness.getAll(ids);
		return R.ok(coreConfigSystemDatas);
	}

	@GetMapping(value = "/get/giatri")
	public R<String> getGiaTriByCode(@RequestParam(name = "code", required = false) String code) {
		return R.ok(coreConfigSystemBusiness.getGiaTriByCode(code));
	}

	@PutMapping(value = {"/{id}"})
	public R<CoreConfigSystemData> update(@PathVariable("id") Long id, @Valid @RequestBody CoreConfigSystemData coreConfigSystemData) {
		coreConfigSystemData = coreConfigSystemBusiness.update(id, coreConfigSystemData);
		return R.ok(coreConfigSystemData);
	}
}
