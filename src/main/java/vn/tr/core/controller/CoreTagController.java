package vn.tr.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.common.encrypt.annotation.ApiEncrypt;
import vn.tr.common.log.annotation.Log;
import vn.tr.common.log.enums.BusinessType;
import vn.tr.common.web.data.dto.DeleteData;
import vn.tr.common.web.utils.PagedResult;
import vn.tr.core.business.CoreTagBusiness;
import vn.tr.core.data.criteria.CoreTagSearchCriteria;
import vn.tr.core.data.dto.CoreTagData;
import vn.tr.core.data.validator.CoreTagValidator;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/tags")
@RequiredArgsConstructor
public class CoreTagController {
	
	private final CoreTagBusiness coreTagBusiness;
	private final CoreTagValidator coreTagValidator;
	
	@InitBinder("coreTagData")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(coreTagValidator);
	}
	
	@PostMapping(value = {""})
	@Log(title = "create CoreTag", businessType = BusinessType.DELETE)
	public R<CoreTagData> create(@Valid @RequestBody CoreTagData coreTagData) {
		coreTagData = coreTagBusiness.create(coreTagData);
		return R.ok(coreTagData);
	}
	
	@DeleteMapping(value = {"/{id}"})
	@Log(title = "Delete CoreTag", businessType = BusinessType.DELETE)
	public R<Void> delete(@PathVariable("id") Long id) {
		coreTagBusiness.delete(id);
		return R.ok();
	}
	
	@DeleteMapping(value = "")
	@Log(title = "bulkDelete CoreTag", businessType = BusinessType.DELETE)
	public R<Void> bulkDelete(@Valid @RequestBody DeleteData deleteData) {
		if (deleteData.getIds() == null || deleteData.getIds().isEmpty()) {
			return R.fail("Danh sách ID không được để trống.");
		}
		coreTagBusiness.bulkDelete(deleteData.getIds());
		return R.ok();
	}
	
	@ApiEncrypt(response = true)
	@GetMapping(value = {"/", ""})
	@Log(title = "FindAll CoreTag", businessType = BusinessType.FINDALL, isSaveRequestData = false)
	public R<PagedResult<CoreTagData>> findAll(CoreTagSearchCriteria criteria) {
		PagedResult<CoreTagData> pageCoreTagData = coreTagBusiness.findAll(criteria);
		return R.ok(pageCoreTagData);
	}
	
	@GetMapping(value = {"/list"})
	@Log(title = "getAll CoreTag", businessType = BusinessType.FINDALL, isSaveRequestData = false)
	public R<List<CoreTagData>> getAll(CoreTagSearchCriteria criteria) {
		List<CoreTagData> coreTagDatas = coreTagBusiness.getAll(criteria);
		return R.ok(coreTagDatas);
	}
	
	@GetMapping(value = {"/{id}"})
	@Log(title = "findById CoreTag", businessType = BusinessType.DETAIL, isSaveRequestData = false)
	public R<CoreTagData> findById(@PathVariable("id") long id) {
		CoreTagData coreTagData = coreTagBusiness.findById(id);
		return R.ok(coreTagData);
	}
	
	@GetMapping(value = {"/get/{id}"})
	public R<CoreTagData> getById(@PathVariable("id") Long id) {
		Optional<CoreTagData> optionalCoreTagData = coreTagBusiness.getById(id);
		return R.ok(optionalCoreTagData.orElse(null));
	}
	
	@PutMapping(value = {"/{id}"})
	@Log(title = "update CoreTag", businessType = BusinessType.UPDATE)
	public R<CoreTagData> update(@PathVariable("id") Long id, @Valid @RequestBody CoreTagData coreTagData) {
		coreTagData = coreTagBusiness.update(id, coreTagData);
		return R.ok(coreTagData);
	}
	
}
