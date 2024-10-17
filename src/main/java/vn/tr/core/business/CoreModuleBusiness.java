package vn.tr.core.business;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import vn.tr.common.core.constant.Constants;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.feign.bean.FileDinhKem;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.core.dao.model.CoreModule;
import vn.tr.core.dao.service.CoreModuleService;
import vn.tr.core.data.CoreAttachmentData;
import vn.tr.core.data.CoreModuleData;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoreModuleBusiness {
	private final CoreModuleService coreModuleService;
	private final CoreAttachmentBusiness coreAttachmentBusiness;

	private CoreModuleData convertToCoreModuleData(CoreModule coreModule, boolean isFindById) {
		CoreModuleData coreModuleData = new CoreModuleData();
		coreModuleData.setId(coreModule.getId());
		coreModuleData.setTen(coreModule.getTen());
		coreModuleData.setMa(coreModule.getMa());
		coreModuleData.setChaId(null);
		if (Objects.nonNull(coreModule.getChaId())) {
			Optional<CoreModule> optionalCoreModule = coreModuleService.findByIdAndDaXoaFalse(coreModule.getChaId());
			if (optionalCoreModule.isPresent()) {
				coreModuleData.setChaId(optionalCoreModule.get().getId());
				coreModuleData.setChaTen(optionalCoreModule.get().getTen());
			}
		}
		coreModuleData.setTrangThai(coreModule.getTrangThai());
		coreModuleData.setSapXep(coreModule.getSapXep());
		if (isFindById) {
			int type = Constants.DINH_KEM_1_FILE;
			Long objectId = coreModule.getId();
			String appCode = CoreModule.class.getSimpleName();
			Long fileDinhKemId = coreModule.getFileDinhKemId();
			FileDinhKem fileDinhKem = coreAttachmentBusiness.getAttachments(fileDinhKemId, appCode, objectId, type);
			coreModuleData.setFileDinhKem(fileDinhKem);
			coreModuleData.setFileDinhKemIds(fileDinhKem.getIds());
			CoreAttachmentData coreAttachmentData = coreAttachmentBusiness.getById(fileDinhKemId);
			coreModuleData.setHinhAnh(coreAttachmentData.getBase64());
		}
		return coreModuleData;
	}

	public CoreModuleData create(CoreModuleData coreModuleData) {
		CoreModule coreModule = new CoreModule();
		return save(coreModule, coreModuleData);
	}

	public void delete(@PathVariable("id") Long id) throws EntityNotFoundException {
		Optional<CoreModule> optional = coreModuleService.findById(id);
		if (optional.isEmpty()) {
			throw new EntityNotFoundException(CoreModule.class, id);
		}
		CoreModule coreModule = optional.get();
		coreModule.setDaXoa(true);
		coreModuleService.save(coreModule);
	}

	public void deleteByIds(List<Long> ids) {
		if (CollUtil.isNotEmpty(ids)) {
			coreModuleService.setFixedDaXoaForIds(true, ids);
		}
	}

	public Page<CoreModuleData> findAll(int page, int size, String sortBy, String sortDir, String search, Boolean trangThai) {
		Pageable pageable = CoreUtils.getPageRequest(page, size, sortBy, sortDir);
		Page<CoreModule> pageCoreModule = coreModuleService.findAll(search, trangThai, pageable);
		return pageCoreModule.map(e -> convertToCoreModuleData(e, false));
	}

	public CoreModuleData findById(Long id) throws EntityNotFoundException {
		Optional<CoreModule> optional = coreModuleService.findById(id);
		if (optional.isEmpty()) {
			throw new EntityNotFoundException(CoreModule.class, id);
		}
		CoreModule coreModule = optional.get();
		return convertToCoreModuleData(coreModule, true);
	}

	public List<CoreModuleData> getAll(List<Long> ids) {
		if (CollUtil.isNotEmpty(ids)) {
			return coreModuleService.findByIdInAndTrangThaiTrueAndDaXoaFalse(ids).stream().map(e -> convertToCoreModuleData(e, true))
					.sorted(Comparator.comparingInt(CoreModuleData::getSapXep)).toList();
		}
		return coreModuleService.findByTrangThaiTrueAndDaXoaFalse().stream().map(e -> convertToCoreModuleData(e, true))
				.sorted(Comparator.comparingInt(CoreModuleData::getSapXep)).toList();
	}

	private CoreModuleData save(CoreModule coreModule, CoreModuleData coreModuleData) {
		coreModule.setDaXoa(false);
		coreModule.setTen(coreModuleData.getTen());
		coreModule.setMa(coreModuleData.getMa());
		coreModule.setChaId(null);
		if (Objects.nonNull(coreModuleData.getChaId())) {
			Optional<CoreModule> optionalCoreModule = coreModuleService.findByIdAndDaXoaFalse(coreModuleData.getChaId());
			if (optionalCoreModule.isPresent()) {
				coreModule.setChaId(optionalCoreModule.get().getId());
			}
		}
		coreModule.setTrangThai(coreModuleData.getTrangThai());
		coreModule.setSapXep(coreModuleData.getSapXep());
		coreModule = coreModuleService.save(coreModule);
		/* Begin đính kèm file *******************************************************/

		/*
		 * Khởi tạo biến **************************************************************
		 * - fileDinhKemIds: danh sách id file đã đính kèm ****************************
		 * - type: loại đính kèm (DINH_KEM_1_FILE || DINH_KEM_NHIEU_FILE) *************
		 * - objectId: id đối tượng đính kèm ******************************************
		 * - appCode: tên model của đối tượng đính kèm*********************************
		 */
		List<Long> fileDinhKemIds = coreModuleData.getFileDinhKemIds();
		int type = Constants.DINH_KEM_1_FILE;
		long objectId = coreModule.getId();
		String appCode = CoreModule.class.getSimpleName();
		/* xử lý dữ liệu cũ */
		coreModule.setFileDinhKemId(null);
		/* xử lý dữ liệu mới */
		List<Long> fileIds = coreAttachmentBusiness.saveAttachments(fileDinhKemIds, appCode, objectId, type);
		if (CollUtil.isNotEmpty(fileIds)) {
			coreModule.setFileDinhKemId(fileIds.getFirst());
			coreModuleService.save(coreModule);
		}

		return convertToCoreModuleData(coreModule, true);
	}

	public CoreModuleData update(Long id, CoreModuleData coreModuleData) throws EntityNotFoundException {
		Optional<CoreModule> optionalCoreModule = coreModuleService.findById(id);
		if (optionalCoreModule.isEmpty()) {
			throw new EntityNotFoundException(CoreModule.class, id);
		}
		CoreModule coreModule = optionalCoreModule.get();
		return save(coreModule, coreModuleData);
	}

}
