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
import vn.tr.core.business.CoreAppBusiness;
import vn.tr.core.data.criteria.CoreAppSearchCriteria;
import vn.tr.core.data.dto.CoreAppData;
import vn.tr.core.data.validator.CoreAppValidator;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/apps")
@RequiredArgsConstructor
public class CoreAppController {
	
	private final CoreAppBusiness coreAppBusiness;
	private final CoreAppValidator coreAppValidator;
	
	@InitBinder("coreAppData")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(coreAppValidator);
	}
	
	@PostMapping(value = {""})
	@Log(title = "create CoreApp", businessType = BusinessType.DELETE)
	public R<CoreAppData> create(@Valid @RequestBody CoreAppData coreAppData) {
		coreAppData = coreAppBusiness.create(coreAppData);
		return R.ok(coreAppData);
	}
	
	@DeleteMapping(value = {"/{id}"})
	@Log(title = "Delete CoreApp", businessType = BusinessType.DELETE)
	public R<Void> delete(@PathVariable("id") Long id) {
		coreAppBusiness.delete(id);
		return R.ok();
	}
	
	@DeleteMapping(value = "")
	@Log(title = "bulkDelete CoreApp", businessType = BusinessType.DELETE)
	public R<Void> bulkDelete(@Valid @RequestBody DeleteData deleteData) {
		if (deleteData.getIds() == null || deleteData.getIds().isEmpty()) {
			return R.fail("Danh sách ID không được để trống.");
		}
		coreAppBusiness.bulkDelete(deleteData.getIds());
		return R.ok();
	}
	
	@GetMapping(value = {"/", ""})
	@Log(title = "FindAll CoreApp", businessType = BusinessType.FINDALL, isSaveRequestData = false)
	public R<PagedResult<CoreAppData>> findAll(CoreAppSearchCriteria criteria) {
		PagedResult<CoreAppData> pageCoreAppData = coreAppBusiness.findAll(criteria);
		return R.ok(pageCoreAppData);
	}
	
	@GetMapping(value = {"/list"})
	@Log(title = "getAll CoreApp", businessType = BusinessType.FINDALL, isSaveRequestData = false)
	public R<List<CoreAppData>> getAll(CoreAppSearchCriteria criteria) {
		List<CoreAppData> coreAppDatas = coreAppBusiness.getAll(criteria);
		return R.ok(coreAppDatas);
	}
	
	@GetMapping(value = {"/{id}"})
	@Log(title = "findById CoreApp", businessType = BusinessType.DETAIL, isSaveRequestData = false)
	public R<CoreAppData> findById(@PathVariable("id") long id) {
		CoreAppData coreAppData = coreAppBusiness.findById(id);
		return R.ok(coreAppData);
	}
	
	@GetMapping(value = {"/get/{id}"})
	public R<CoreAppData> getById(@PathVariable("id") Long id) {
		Optional<CoreAppData> optionalCoreAppData = coreAppBusiness.getById(id);
		return R.ok(optionalCoreAppData.orElse(null));
	}
	
	@PutMapping(value = {"/{id}"})
	@Log(title = "update CoreApp", businessType = BusinessType.UPDATE)
	public R<CoreAppData> update(@PathVariable("id") Long id, @Valid @RequestBody CoreAppData coreAppData) {
		coreAppData = coreAppBusiness.update(id, coreAppData);
		return R.ok(coreAppData);
	}
	
}
