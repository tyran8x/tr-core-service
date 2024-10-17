package vn.tr.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.core.business.CoreModuleBusiness;
import vn.tr.core.data.CoreModuleData;
import vn.tr.core.data.validator.CoreModuleValidator;

import java.util.List;

@RestController
@RequestMapping(value = "/module")
@RequiredArgsConstructor
public class CoreModuleController {

	private final CoreModuleBusiness coreModuleBusiness;
	private final CoreModuleValidator coreModuleValidator;

	@PostMapping(value = {""})
	public R<CoreModuleData> create(@Valid @RequestBody CoreModuleData coreModuleData) {
		coreModuleData = coreModuleBusiness.create(coreModuleData);
		return R.ok(coreModuleData);
	}

	@DeleteMapping(value = {"/{id}"})
	public R<Void> delete(@PathVariable("id") Long id) {
		coreModuleBusiness.delete(id);
		return R.ok();
	}

	@DeleteMapping(value = {"/ids"})
	public R<Void> deleteByIds(@RequestParam(name = "ids") List<Long> ids) {
		coreModuleBusiness.deleteByIds(ids);
		return R.ok();
	}

	@GetMapping(value = {"/", ""})
	public R<Page<CoreModuleData>> findAll(
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "20", required = false) int size,
			@RequestParam(name = "sortBy", defaultValue = "ten", required = false) String sortBy,
			@RequestParam(name = "sortDir", defaultValue = "ASC", required = false) String sortDir,
			@RequestParam(name = "search", required = false) String search,
			@RequestParam(name = "trangThai", required = false) Boolean trangThai) {
		Page<CoreModuleData> pageCoreModuleData = coreModuleBusiness.findAll(page, size, sortBy, sortDir, search, trangThai);
		return R.ok(pageCoreModuleData);
	}

	@GetMapping(value = "/{id}")
	public R<CoreModuleData> findById(@PathVariable("id") Long id) {
		CoreModuleData coreModuleData = coreModuleBusiness.findById(id);
		return R.ok(coreModuleData);
	}

	@GetMapping(value = "/get")
	public R<List<CoreModuleData>> getAll(@RequestParam(name = "ids", required = false) List<Long> ids) {
		List<CoreModuleData> coreModuleDatas = coreModuleBusiness.getAll(ids);
		return R.ok(coreModuleDatas);
	}

	@InitBinder
	private void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(coreModuleValidator);
	}

	@PutMapping(value = {"/{id}"})
	public R<CoreModuleData> update(@PathVariable("id") Long id, @Valid @RequestBody CoreModuleData coreModuleData) {
		coreModuleData = coreModuleBusiness.update(id, coreModuleData);
		return R.ok(coreModuleData);
	}
}
