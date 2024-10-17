package vn.tr.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.core.business.CoreUserManualBusiness;
import vn.tr.core.data.CoreUserManualData;

@RestController
@RequestMapping(value = "/usermanual")
@RequiredArgsConstructor
public class CoreUserManualController {

	private final CoreUserManualBusiness coreUserManualBusiness;

	@GetMapping(value = {"/", ""})
	public R<Page<CoreUserManualData>> findAll(
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "20", required = false) int size,
			@RequestParam(name = "sortBy", defaultValue = "sapXep", required = false) String sortBy,
			@RequestParam(name = "sortDir", defaultValue = "ASC", required = false) String sortDir,
			@RequestParam(name = "search", required = false) String search,
			@RequestParam(name = "trangThai", required = false) Boolean trangThai,
			@RequestParam(name = "appCode", required = false) String appCode) {
		Page<CoreUserManualData> pageCoreUserManualData = coreUserManualBusiness.findAll(page, size, sortBy, sortDir, search, trangThai, appCode);
		return R.ok(pageCoreUserManualData);
	}

	@GetMapping(value = "/{id}")
	public R<CoreUserManualData> findById(@PathVariable("id") Long id) {
		CoreUserManualData coreUserManual = coreUserManualBusiness.findById(id);
		return R.ok(coreUserManual);
	}

	@PostMapping(value = {""})
	public R<CoreUserManualData> create(@Valid @RequestBody CoreUserManualData coreUserManualData) {
		coreUserManualData = coreUserManualBusiness.create(coreUserManualData);
		return R.ok(coreUserManualData);
	}

	@PutMapping(value = {"/{id}"})
	public R<CoreUserManualData> update(@PathVariable("id") Long id, @Valid @RequestBody CoreUserManualData coreUserManualData) {
		coreUserManualData = coreUserManualBusiness.update(id, coreUserManualData);
		return R.ok(coreUserManualData);
	}

	@DeleteMapping(value = {"/{id}"})
	public R<Void> delete(@PathVariable("id") Long id) {
		coreUserManualBusiness.delete(id);
		return R.ok();
	}

}
