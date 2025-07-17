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
import vn.tr.core.business.CoreGroupBusiness;
import vn.tr.core.data.criteria.CoreGroupSearchCriteria;
import vn.tr.core.data.dto.CoreGroupData;
import vn.tr.core.data.validator.CoreGroupValidator;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/groups")
@RequiredArgsConstructor
public class CoreGroupController {
	
	private final CoreGroupBusiness coreGroupBusiness;
	private final CoreGroupValidator coreGroupValidator;
	
	@InitBinder("coreGroupData")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(coreGroupValidator);
	}
	
	@PostMapping(value = {""})
	@Log(title = "create CoreGroup", businessType = BusinessType.DELETE)
	public R<CoreGroupData> create(@Valid @RequestBody CoreGroupData coreGroupData) {
		coreGroupData = coreGroupBusiness.create(coreGroupData);
		return R.ok(coreGroupData);
	}
	
	@DeleteMapping(value = {"/{id}"})
	@Log(title = "Delete CoreGroup", businessType = BusinessType.DELETE)
	public R<Void> delete(@PathVariable("id") Long id) {
		coreGroupBusiness.delete(id);
		return R.ok();
	}
	
	@DeleteMapping(value = "")
	@Log(title = "bulkDelete CoreGroup", businessType = BusinessType.DELETE)
	public R<Void> bulkDelete(@Valid @RequestBody DeleteData deleteData) {
		if (deleteData.getIds() == null || deleteData.getIds().isEmpty()) {
			return R.fail("Danh sách ID không được để trống.");
		}
		coreGroupBusiness.bulkDelete(deleteData.getIds());
		return R.ok();
	}
	
	@GetMapping(value = {"/", ""})
	@Log(title = "FindAll CoreGroup", businessType = BusinessType.FINDALL, isSaveRequestData = false)
	public R<Page<CoreGroupData>> findAll(CoreGroupSearchCriteria criteria) {
		Page<CoreGroupData> pageCoreGroupData = coreGroupBusiness.findAll(criteria);
		return R.ok(pageCoreGroupData);
	}
	
	@GetMapping(value = {"/list"})
	@Log(title = "getAll CoreGroup", businessType = BusinessType.FINDALL, isSaveRequestData = false)
	public R<List<CoreGroupData>> getAll(CoreGroupSearchCriteria criteria) {
		List<CoreGroupData> coreGroupDatas = coreGroupBusiness.getAll(criteria);
		return R.ok(coreGroupDatas);
	}
	
	@GetMapping(value = {"/{id}"})
	@Log(title = "findById CoreGroup", businessType = BusinessType.DETAIL, isSaveRequestData = false)
	public R<CoreGroupData> findById(@PathVariable("id") long id) {
		CoreGroupData coreGroupData = coreGroupBusiness.findById(id);
		return R.ok(coreGroupData);
	}
	
	@GetMapping(value = {"/get/{id}"})
	public R<CoreGroupData> getById(@PathVariable("id") Long id) {
		Optional<CoreGroupData> optionalCoreGroupData = coreGroupBusiness.getById(id);
		return R.ok(optionalCoreGroupData.orElse(null));
	}
	
	@PutMapping(value = {"/{id}"})
	@Log(title = "update CoreGroup", businessType = BusinessType.UPDATE)
	public R<CoreGroupData> update(@PathVariable("id") Long id, @Valid @RequestBody CoreGroupData coreGroupData) {
		coreGroupData = coreGroupBusiness.update(id, coreGroupData);
		return R.ok(coreGroupData);
	}
	
}
