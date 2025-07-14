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
import vn.tr.core.business.CoreGroupBusiness;
import vn.tr.core.data.CoreGroupData;
import vn.tr.core.data.criteria.CoreGroupSearchCriteria;
import vn.tr.core.data.validator.CoreGroupValidator;

@RestController
@RequestMapping(value = "/group")
@RequiredArgsConstructor
public class CoreGroupController {
	
	private final CoreGroupBusiness coreGroupBusiness;
	private final CoreGroupValidator coreGroupValidator;
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		if (binder.getTarget() != null && CoreGroupData.class.equals(binder.getTarget().getClass())) {
			binder.addValidators(coreGroupValidator);
		}
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
	public R<Page<CoreGroupData>> findAll(CoreGroupSearchCriteria criteria) {
		Page<CoreGroupData> pageCoreGroupData = coreGroupBusiness.findAll(criteria);
		return R.ok(pageCoreGroupData);
	}
	
	@GetMapping(value = {"/{id}"})
	public R<CoreGroupData> findById(@PathVariable("id") long id) throws EntityNotFoundException {
		CoreGroupData coreGroupData = coreGroupBusiness.findById(id);
		return R.ok(coreGroupData);
	}
	
	@PutMapping(value = {"/{id}"})
	public R<CoreGroupData> update(@PathVariable("id") Long id, @Valid @RequestBody CoreGroupData coreGroupData) throws EntityNotFoundException {
		coreGroupData = coreGroupBusiness.update(id, coreGroupData);
		return R.ok(coreGroupData);
	}
	
}
