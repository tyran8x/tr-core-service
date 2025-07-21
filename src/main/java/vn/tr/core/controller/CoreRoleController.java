package vn.tr.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.common.log.annotation.Log;
import vn.tr.common.log.enums.BusinessType;
import vn.tr.common.web.data.dto.DeleteData;
import vn.tr.common.web.utils.PagedResult;
import vn.tr.core.business.CoreRoleBusiness;
import vn.tr.core.data.criteria.CoreRoleSearchCriteria;
import vn.tr.core.data.dto.CoreRoleData;
import vn.tr.core.data.validator.CoreRoleValidator;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/roles")
@RequiredArgsConstructor
public class CoreRoleController {
	
	private final CoreRoleBusiness coreRoleBusiness;
	private final CoreRoleValidator coreRoleValidator;
	
	@InitBinder("coreRoleData")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(coreRoleValidator);
	}
	
	@PostMapping(value = {""})
	@Log(title = "create CoreRole", businessType = BusinessType.DELETE)
	public R<CoreRoleData> create(@Valid @RequestBody CoreRoleData coreRoleData) {
		coreRoleData = coreRoleBusiness.create(coreRoleData);
		return R.ok(coreRoleData);
	}
	
	@DeleteMapping(value = {"/{id}"})
	@Log(title = "Delete CoreRole", businessType = BusinessType.DELETE)
	public R<Void> delete(@PathVariable("id") Long id) {
		coreRoleBusiness.delete(id);
		return R.ok();
	}
	
	@DeleteMapping(value = "")
	@Log(title = "bulkDelete CoreRole", businessType = BusinessType.DELETE)
	public R<Void> bulkDelete(@Valid @RequestBody DeleteData deleteData) {
		if (deleteData.getIds() == null || deleteData.getIds().isEmpty()) {
			return R.fail("Danh sách ID không được để trống.");
		}
		coreRoleBusiness.bulkDelete(deleteData.getIds());
		return R.ok();
	}
	
	@GetMapping(value = {"/", ""})
	@Log(title = "FindAll CoreRole", businessType = BusinessType.FINDALL, isSaveRequestData = false)
	public R<PagedResult<CoreRoleData>> findAll(CoreRoleSearchCriteria criteria) {
		PagedResult<CoreRoleData> pageCoreRoleData = coreRoleBusiness.findAll(criteria);
		return R.ok(pageCoreRoleData);
	}
	
	@GetMapping(value = {"/list"})
	@Log(title = "getAll CoreRole", businessType = BusinessType.FINDALL, isSaveRequestData = false)
	public R<List<CoreRoleData>> getAll(CoreRoleSearchCriteria criteria) {
		List<CoreRoleData> coreRoleDatas = coreRoleBusiness.getAll(criteria);
		return R.ok(coreRoleDatas);
	}
	
	@GetMapping(value = {"/{id}"})
	@Log(title = "findById CoreRole", businessType = BusinessType.DETAIL, isSaveRequestData = false)
	public R<CoreRoleData> findById(@PathVariable("id") long id) {
		CoreRoleData coreRoleData = coreRoleBusiness.findById(id);
		return R.ok(coreRoleData);
	}
	
	@GetMapping(value = {"/get/{id}"})
	public R<CoreRoleData> getById(@PathVariable("id") Long id) {
		Optional<CoreRoleData> optionalCoreRoleData = coreRoleBusiness.getById(id);
		return R.ok(optionalCoreRoleData.orElse(null));
	}
	
	@PutMapping(value = {"/{id}"})
	@Log(title = "update CoreRole", businessType = BusinessType.UPDATE)
	public R<CoreRoleData> update(@PathVariable("id") Long id, @Valid @RequestBody CoreRoleData coreRoleData) {
		coreRoleData = coreRoleBusiness.update(id, coreRoleData);
		return R.ok(coreRoleData);
	}
	
}
