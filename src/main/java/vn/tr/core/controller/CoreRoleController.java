package vn.tr.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.log.annotation.Log;
import vn.tr.common.log.enums.BusinessType;
import vn.tr.core.business.CoreRoleBusiness;
import vn.tr.core.data.CoreRoleData;
import vn.tr.core.data.validator.CoreRoleValidator;

@RestController
@RequestMapping(value = "/role")
@RequiredArgsConstructor
public class CoreRoleController {
	
	private final CoreRoleBusiness coreRoleBusiness;
	private final CoreRoleValidator coreRoleValidator;
	
	@PostMapping(value = {""})
	public R<CoreRoleData> create(@Valid @RequestBody CoreRoleData coreRoleData) {
		coreRoleData = coreRoleBusiness.create(coreRoleData);
		return R.ok(coreRoleData);
	}
	
	@DeleteMapping(value = {"/{id}"})
	public R<Void> delete(@PathVariable("id") Long id) throws EntityNotFoundException {
		coreRoleBusiness.delete(id);
		return R.ok();
	}
	
	@GetMapping(value = {"/", ""})
	@Log(title = "FindAll CoreRole", businessType = BusinessType.OTHER, isSaveRequestData = false)
	public R<Page<CoreRoleData>> findAll(
			@RequestHeader(name = "X-App-Code", required = false) String xAppCode,
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "20", required = false) int size,
			@RequestParam(name = "sortBy", defaultValue = "ngayCapNhat", required = false) String sortBy,
			@RequestParam(name = "sortDir", defaultValue = "DESC", required = false) String sortDir,
			@RequestParam(name = "search", required = false) String search,
			@RequestParam(name = "trangThai", required = false) Boolean trangThai) {
		Page<CoreRoleData> pageCoreRoleData = coreRoleBusiness.findAll(page, size, sortBy, sortDir, search, trangThai, xAppCode);
		return R.ok(pageCoreRoleData);
	}
	
	@GetMapping(value = {"/{id}"})
	public R<CoreRoleData> findById(@PathVariable("id") long id) throws EntityNotFoundException {
		CoreRoleData coreRoleData = coreRoleBusiness.findById(id);
		return R.ok(coreRoleData);
	}
	
	@InitBinder
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(coreRoleValidator);
	}
	
	@PutMapping(value = {"/{id}"})
	public R<CoreRoleData> update(@PathVariable("id") Long id, @Valid @RequestBody CoreRoleData coreRoleData) throws EntityNotFoundException {
		coreRoleData = coreRoleBusiness.update(id, coreRoleData);
		return R.ok(coreRoleData);
	}
	
}
