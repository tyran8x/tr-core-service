package vn.tr.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.common.log.annotation.Log;
import vn.tr.common.log.enums.BusinessType;
import vn.tr.common.web.data.dto.DeleteData;
import vn.tr.core.business.CoreModuleBusiness;
import vn.tr.core.data.criteria.CoreModuleSearchCriteria;
import vn.tr.core.data.dto.CoreModuleData;
import vn.tr.core.data.validator.CoreModuleValidator;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/module")
@RequiredArgsConstructor
public class CoreModuleController {
	
	private final CoreModuleBusiness coreModuleBusiness;
	private final CoreModuleValidator coreModuleValidator;
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		if (binder.getTarget() != null && CoreModuleData.class.equals(binder.getTarget().getClass())) {
			binder.addValidators(coreModuleValidator);
		}
	}
	
	@PostMapping(value = {""})
	@Log(title = "create CoreModule", businessType = BusinessType.DELETE)
	public R<CoreModuleData> create(@Valid @RequestBody CoreModuleData coreModuleData) {
		coreModuleData = coreModuleBusiness.create(coreModuleData);
		return R.ok(coreModuleData);
	}
	
	@DeleteMapping(value = {"/{id}"})
	@Log(title = "Delete CoreModule", businessType = BusinessType.DELETE)
	public R<Void> delete(@PathVariable("id") Long id) {
		coreModuleBusiness.delete(id);
		return R.ok();
	}
	
	@DeleteMapping(value = "")
	@Log(title = "bulkDelete CoreModule", businessType = BusinessType.DELETE)
	public R<Void> bulkDelete(@Valid @RequestBody DeleteData deleteData) {
		if (deleteData.getIds() == null || deleteData.getIds().isEmpty()) {
			return R.fail("Danh sách ID không được để trống.");
		}
		coreModuleBusiness.bulkDelete(deleteData.getIds());
		return R.ok();
	}
	
	@GetMapping(value = {"/", ""})
	@Log(title = "FindAll CoreModule", businessType = BusinessType.FINDALL, isSaveRequestData = false)
	public R<Page<CoreModuleData>> findAll(CoreModuleSearchCriteria criteria) {
		Page<CoreModuleData> pageCoreModuleData = coreModuleBusiness.findAll(criteria);
		return R.ok(pageCoreModuleData);
	}
	
	@GetMapping(value = {"/list"})
	@Log(title = "getAll CoreModule", businessType = BusinessType.FINDALL, isSaveRequestData = false)
	public R<List<CoreModuleData>> getAll(CoreModuleSearchCriteria criteria) {
		List<CoreModuleData> coreModuleDatas = coreModuleBusiness.getAll(criteria);
		return R.ok(coreModuleDatas);
	}
	
	@GetMapping(value = {"/{id}"})
	@Log(title = "findById CoreModule", businessType = BusinessType.DETAIL, isSaveRequestData = false)
	public R<CoreModuleData> findById(@PathVariable("id") long id) {
		CoreModuleData coreModuleData = coreModuleBusiness.findById(id);
		return R.ok(coreModuleData);
	}
	
	@GetMapping(value = {"/get/{id}"})
	public R<CoreModuleData> getById(@PathVariable("id") Long id) {
		Optional<CoreModuleData> optionalCoreModuleData = coreModuleBusiness.getById(id);
		return R.ok(optionalCoreModuleData.orElse(null));
	}
	
	@PutMapping(value = {"/{id}"})
	@Log(title = "update CoreModule", businessType = BusinessType.UPDATE)
	public R<CoreModuleData> update(@PathVariable("id") Long id, @Valid @RequestBody CoreModuleData coreModuleData) {
		coreModuleData = coreModuleBusiness.update(id, coreModuleData);
		return R.ok(coreModuleData);
	}
	
}
