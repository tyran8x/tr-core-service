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
import vn.tr.core.business.CoreWorkSpaceItemBusiness;
import vn.tr.core.data.criteria.CoreWorkSpaceItemSearchCriteria;
import vn.tr.core.data.dto.CoreWorkSpaceItemData;
import vn.tr.core.data.validator.CoreWorkSpaceItemValidator;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/workspace/items")
@RequiredArgsConstructor
public class CoreWorkSpaceItemController {
	
	private final CoreWorkSpaceItemBusiness coreWorkSpaceItemBusiness;
	private final CoreWorkSpaceItemValidator coreWorkSpaceItemValidator;
	
	@InitBinder("coreWorkSpaceItemData")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(coreWorkSpaceItemValidator);
	}
	
	@PostMapping(value = {""})
	@Log(title = "create CoreWorkSpaceItem", businessType = BusinessType.DELETE)
	public R<CoreWorkSpaceItemData> create(@Valid @RequestBody CoreWorkSpaceItemData coreWorkSpaceItemData) {
		coreWorkSpaceItemData = coreWorkSpaceItemBusiness.create(coreWorkSpaceItemData);
		return R.ok(coreWorkSpaceItemData);
	}
	
	@DeleteMapping(value = {"/{id}"})
	@Log(title = "Delete CoreWorkSpaceItem", businessType = BusinessType.DELETE)
	public R<Void> delete(@PathVariable("id") Long id) {
		coreWorkSpaceItemBusiness.delete(id);
		return R.ok();
	}
	
	@DeleteMapping(value = "")
	@Log(title = "bulkDelete CoreWorkSpaceItem", businessType = BusinessType.DELETE)
	public R<Void> bulkDelete(@Valid @RequestBody DeleteData deleteData) {
		if (deleteData.getIds() == null || deleteData.getIds().isEmpty()) {
			return R.fail("Danh sách ID không được để trống.");
		}
		coreWorkSpaceItemBusiness.bulkDelete(deleteData.getIds());
		return R.ok();
	}
	
	@GetMapping(value = {"/", ""})
	@Log(title = "FindAll CoreWorkSpaceItem", businessType = BusinessType.FINDALL, isSaveRequestData = false)
	public R<Page<CoreWorkSpaceItemData>> findAll(CoreWorkSpaceItemSearchCriteria criteria) {
		Page<CoreWorkSpaceItemData> pageCoreWorkSpaceItemData = coreWorkSpaceItemBusiness.findAll(criteria);
		return R.ok(pageCoreWorkSpaceItemData);
	}
	
	@GetMapping(value = {"/list"})
	@Log(title = "getAll CoreWorkSpaceItem", businessType = BusinessType.FINDALL, isSaveRequestData = false)
	public R<List<CoreWorkSpaceItemData>> getAll(CoreWorkSpaceItemSearchCriteria criteria) {
		List<CoreWorkSpaceItemData> coreWorkSpaceItemDatas = coreWorkSpaceItemBusiness.getAll(criteria);
		return R.ok(coreWorkSpaceItemDatas);
	}
	
	@GetMapping(value = {"/{id}"})
	@Log(title = "findById CoreWorkSpaceItem", businessType = BusinessType.DETAIL, isSaveRequestData = false)
	public R<CoreWorkSpaceItemData> findById(@PathVariable("id") long id) {
		CoreWorkSpaceItemData coreWorkSpaceItemData = coreWorkSpaceItemBusiness.findById(id);
		return R.ok(coreWorkSpaceItemData);
	}
	
	@GetMapping(value = {"/get/{id}"})
	public R<CoreWorkSpaceItemData> getById(@PathVariable("id") Long id) {
		Optional<CoreWorkSpaceItemData> optionalCoreWorkSpaceItemData = coreWorkSpaceItemBusiness.getById(id);
		return R.ok(optionalCoreWorkSpaceItemData.orElse(null));
	}
	
	@PutMapping(value = {"/{id}"})
	@Log(title = "update CoreWorkSpaceItem", businessType = BusinessType.UPDATE)
	public R<CoreWorkSpaceItemData> update(@PathVariable("id") Long id, @Valid @RequestBody CoreWorkSpaceItemData coreWorkSpaceItemData) {
		coreWorkSpaceItemData = coreWorkSpaceItemBusiness.update(id, coreWorkSpaceItemData);
		return R.ok(coreWorkSpaceItemData);
	}
	
}
