package vn.tr.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.core.business.CoreUserBusiness;
import vn.tr.core.data.CoreUserData;
import vn.tr.core.data.validator.CoreUserValidator;

import java.util.List;

@RestController
@RequestMapping(value = "/user")
@RequiredArgsConstructor
public class CoreUserController {

	private final CoreUserBusiness coreUserBusiness;
	private final CoreUserValidator coreUserValidator;

	@PostMapping(value = {""})
	public R<CoreUserData> create(@Valid @RequestBody CoreUserData coreUserData) {
		coreUserData = coreUserBusiness.create(coreUserData);
		return R.ok(coreUserData);
	}

	@DeleteMapping(value = {"/{id}"})
	public R<Void> delete(@PathVariable("id") Long id) throws EntityNotFoundException {
		coreUserBusiness.delete(id);
		return R.ok();
	}

	@GetMapping(value = {"/", ""})
	public R<Page<CoreUserData>> findAll(
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "20", required = false) int size,
			@RequestParam(name = "sortBy", defaultValue = "email", required = false) String sortBy,
			@RequestParam(name = "sortDir", defaultValue = "ASC", required = false) String sortDir,
			@RequestParam(name = "appCode", required = false) String appCode,
			@RequestParam(name = "roleIds", required = false) List<Long> roleIds,
			@RequestParam(name = "email", required = false) String email,
			@RequestParam(name = "name", required = false) String name) {
		Page<CoreUserData> pageCoreUserData = coreUserBusiness.findAll(page, size, sortBy, sortDir, email, name, roleIds, appCode);
		return R.ok(pageCoreUserData);
	}

	@GetMapping(value = {"/email/{email}"})
	public R<CoreUserData> findByEmail(@PathVariable("email") String email) {
		CoreUserData coreUserData = coreUserBusiness.findByEmail(email);
		return R.ok(coreUserData);
	}

	@GetMapping(value = {"/{id}"})
	public R<CoreUserData> findById(@PathVariable("id") long id) throws EntityNotFoundException {
		CoreUserData coreUserData = coreUserBusiness.findById(id);
		return R.ok(coreUserData);
	}

	@InitBinder
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(coreUserValidator);
	}

	@PutMapping(value = {"/{id}"})
	public R<CoreUserData> update(@PathVariable("id") Long id, @Valid @RequestBody CoreUserData coreUserData) throws EntityNotFoundException {
		coreUserData = coreUserBusiness.update(id, coreUserData);
		return R.ok(coreUserData);
	}
}
