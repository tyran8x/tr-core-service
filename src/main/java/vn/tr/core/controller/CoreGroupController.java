package vn.tr.core.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.log.annotation.Log;
import vn.tr.common.log.enums.BusinessType;
import vn.tr.core.business.CoreGroupBusiness;
import vn.tr.core.data.CoreGroupData;
import vn.tr.core.data.validator.CoreGroupValidator;

@RestController
@RequestMapping(value = "/group")
public class CoreGroupController {

	private final CoreGroupBusiness coreGroupBusiness;
	private final CoreGroupValidator coreGroupValidator;

	public CoreGroupController(CoreGroupBusiness coreGroupBusiness, CoreGroupValidator coreGroupValidator) {
		this.coreGroupBusiness = coreGroupBusiness;
		this.coreGroupValidator = coreGroupValidator;
	}

	@PostMapping(value = {""})
	public R<CoreGroupData> create(@Valid @RequestBody CoreGroupData coreGroupData) {
		coreGroupData = coreGroupBusiness.create(coreGroupData);
		return R.ok(coreGroupData);
	}

	@DeleteMapping(value = {"/{id}"})
	public R<Void> delete(@PathVariable("id") Long id) throws EntityNotFoundException {
		coreGroupBusiness.delete(id);
		return R.ok();
	}

	@GetMapping(value = {"/", ""})
	@Log(title = "FindAll CoreGroup", businessType = BusinessType.OTHER, isSaveRequestData = false)
	public R<Page<CoreGroupData>> findAll(
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "20", required = false) int size,
			@RequestParam(name = "sortBy", defaultValue = "ngayCapNhat", required = false) String sortBy,
			@RequestParam(name = "sortDir", defaultValue = "DESC", required = false) String sortDir,
			@RequestParam(name = "appCode", required = false) String appCode,
			@RequestParam(name = "search", required = false) String search,
			@RequestParam(name = "trangThai", required = false) Boolean trangThai) {
		Page<CoreGroupData> pageCoreGroupData = coreGroupBusiness.findAll(page, size, sortBy, sortDir, search, trangThai, appCode);
		return R.ok(pageCoreGroupData);
	}

	@GetMapping(value = {"/{id}"})
	public R<CoreGroupData> findById(@PathVariable("id") long id) throws EntityNotFoundException {
		CoreGroupData coreGroupData = coreGroupBusiness.findById(id);
		return R.ok(coreGroupData);
	}

	@InitBinder
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(coreGroupValidator);
	}

	@PutMapping(value = {"/{id}"})
	public R<CoreGroupData> update(@PathVariable("id") Long id, @Valid @RequestBody CoreGroupData coreGroupData) throws EntityNotFoundException {
		coreGroupData = coreGroupBusiness.update(id, coreGroupData);
		return R.ok(coreGroupData);
	}

}
