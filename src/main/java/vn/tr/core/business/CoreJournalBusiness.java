package vn.tr.core.business;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.core.dao.model.CoreJournal;
import vn.tr.core.dao.model.CoreJournalDetail;
import vn.tr.core.dao.service.CoreJournalDetailService;
import vn.tr.core.dao.service.CoreJournalService;
import vn.tr.core.data.CoreJournalData;
import vn.tr.core.data.CoreJournalDetailData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoreJournalBusiness {
	private final CoreJournalService coreJournalService;

	private final CoreJournalDetailService coreJournalDetailService;

	private CoreJournalData convertToCoreJournalData(CoreJournal coreJournal) {
		CoreJournalData coreJournalData = new CoreJournalData();
		coreJournalData.setId(coreJournal.getId());
		coreJournalData.setObjectId(coreJournal.getObjectId());
		coreJournalData.setObjectType(coreJournal.getObjectType());
		coreJournalData.setAppCode(coreJournal.getAppCode());
		coreJournalData.setNote(coreJournal.getNote());
		coreJournalData.setIsPrivate(coreJournal.getIsPrivate());
		coreJournalData.setNgayTao(coreJournal.getNgayTao());
		coreJournalData.setNguoiTao(coreJournal.getNguoiTao());

		List<CoreJournalDetail> coreJournalDetails = coreJournalDetailService.findByJournalIdAndDaXoaFalse(coreJournal.getId());
		List<CoreJournalDetailData> coreJournalDetailDatas = new ArrayList<>();
		if (CollUtil.isNotEmpty(coreJournalDetails)) {
			for (CoreJournalDetail coreJournalDetail : coreJournalDetails) {
				CoreJournalDetailData coreJournalDetailData = new CoreJournalDetailData();
				coreJournalDetailData.setId(coreJournalDetail.getId());
				coreJournalDetailData.setJournalId(coreJournalDetail.getJournalId());
				coreJournalDetailData.setProperty(coreJournalDetail.getProperty());
				coreJournalDetailData.setPropKey(coreJournalDetail.getPropKey());
				coreJournalDetailData.setOldValue(coreJournalDetail.getOldValue());
				coreJournalDetailData.setNewValue(coreJournalDetail.getNewValue());

				coreJournalDetailDatas.add(coreJournalDetailData);
			}
		}
		coreJournalData.setCoreJournalDetailDatas(coreJournalDetailDatas);
		return coreJournalData;
	}

	public List<CoreJournalData> getCoreJournals(Long objectId, String objectType, String appCode) {
		List<CoreJournal> coreJournals = coreJournalService.findByObjectIdAndObjectTypeAndAppCodeAndDaXoaFalseOrderByNgayTaoDesc(objectId,
				objectType, appCode);
		List<CoreJournalData> coreJournalDatas = new ArrayList<>();

		if (CollUtil.isNotEmpty(coreJournals)) {
			coreJournalDatas = coreJournals.stream().map(this::convertToCoreJournalData).collect(Collectors.toList());
		}
		return coreJournalDatas;
	}

	public CoreJournalData create(CoreJournalData coreJournalData) {
		CoreJournal coreJournal = new CoreJournal();
		return save(coreJournal, coreJournalData);
	}

	public void delete(Long id) throws EntityNotFoundException {
		Optional<CoreJournal> optional = coreJournalService.findById(id);
		if (optional.isEmpty()) {
			throw new EntityNotFoundException(CoreJournal.class, id);
		}
		CoreJournal coreJournal = optional.get();
		coreJournal.setDaXoa(true);
		coreJournalService.save(coreJournal);
	}

	public CoreJournalData findById(Long id) throws EntityNotFoundException {
		Optional<CoreJournal> optionalCoreJournal = coreJournalService.findById(id);
		if (optionalCoreJournal.isEmpty()) {
			throw new EntityNotFoundException(CoreJournal.class, id);
		}
		return convertToCoreJournalData(optionalCoreJournal.get());
	}

	private CoreJournalData save(CoreJournal coreJournal, CoreJournalData coreJournalData) {
		coreJournal.setDaXoa(false);
		coreJournal.setObjectId(coreJournalData.getObjectId());
		coreJournal.setObjectType(coreJournalData.getObjectType());
		coreJournal.setAppCode(coreJournalData.getAppCode());
		coreJournal.setNote(coreJournalData.getNote());
		coreJournal.setNguoiTao(coreJournalData.getNguoiTao());
		coreJournal.setIsPrivate(coreJournalData.getIsPrivate());
		coreJournal = coreJournalService.save(coreJournal);
		coreJournalDetailService.setFixedDaXoaForJournalId(true, coreJournal.getId());
		List<CoreJournalDetailData> coreJournalDetailDatas = coreJournalData.getCoreJournalDetailDatas();
		if (CollUtil.isNotEmpty(coreJournalDetailDatas)) {
			for (CoreJournalDetailData coreJournalDetailData : coreJournalDetailDatas) {
				CoreJournalDetail coreJournalDetail = new CoreJournalDetail();
				coreJournalDetail.setDaXoa(false);
				coreJournalDetail.setJournalId(coreJournal.getId());
				coreJournalDetail.setProperty(coreJournalDetailData.getProperty());
				coreJournalDetail.setPropKey(coreJournalDetailData.getPropKey());
				coreJournalDetail.setOldValue(coreJournalDetailData.getOldValue());
				coreJournalDetail.setNewValue(coreJournalDetailData.getNewValue());
				coreJournalDetailService.save(coreJournalDetail);
			}
		}
		return convertToCoreJournalData(coreJournal);
	}

	public CoreJournalData update(Long id, CoreJournalData coreJournalData) throws EntityNotFoundException {
		Optional<CoreJournal> optionalCoreJournal = coreJournalService.findById(id);
		if (optionalCoreJournal.isEmpty()) {
			throw new EntityNotFoundException(CoreJournal.class, id);
		}
		CoreJournal coreJournal = optionalCoreJournal.get();
		return save(coreJournal, coreJournalData);
	}

}
