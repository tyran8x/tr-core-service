package vn.tr.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.core.business.CoreJournalBusiness;
import vn.tr.core.data.CoreJournalData;

import java.util.List;

@RestController
@RequestMapping(value = "/journal")
@RequiredArgsConstructor
public class CoreJournalController {

	private final CoreJournalBusiness coreJournalBusiness;

	@PostMapping(value = {""})
	public R<CoreJournalData> create(@Valid @RequestBody CoreJournalData coreJournalData) {
		coreJournalData = coreJournalBusiness.create(coreJournalData);
		return R.ok(coreJournalData);
	}

	@DeleteMapping(value = {"/{id}"})
	public R<Void> delete(@PathVariable("id") Long id) {
		coreJournalBusiness.delete(id);
		return R.ok();
	}

	@GetMapping(value = "/{id}")
	public R<CoreJournalData> findById(@PathVariable("id") Long id) {
		CoreJournalData coreJournalData = coreJournalBusiness.findById(id);
		return R.ok(coreJournalData);
	}

	@GetMapping(value = "/get")
	public R<List<CoreJournalData>> getCoreJournals(
			@RequestParam(name = "objectId") Long objectId,
			@RequestParam(name = "objectType") String objectType,
			@RequestParam(name = "appCode") String appCode
	) {
		List<CoreJournalData> coreJournalDatas = coreJournalBusiness.getCoreJournals(objectId, objectType, appCode);
		return R.ok(coreJournalDatas);
	}

	@PutMapping(value = {"/{id}"})
	public R<CoreJournalData> update(@PathVariable("id") Long id, @Valid @RequestBody CoreJournalData coreJournalData) {
		coreJournalData = coreJournalBusiness.update(id, coreJournalData);
		return R.ok(coreJournalData);
	}
}
