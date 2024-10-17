package vn.tr.core.business;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.core.utils.FunctionUtils;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.core.dao.model.CoreGroup;
import vn.tr.core.dao.service.CoreGroupService;
import vn.tr.core.data.CoreGroupData;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoreGroupBusiness {

	private final CoreGroupService coreGroupService;

	private CoreGroupData convertToCoreGroupData(CoreGroup coreGroup) {
		CoreGroupData coreGroupData = new CoreGroupData();
		coreGroupData.setChaId(coreGroup.getChaId());
		coreGroupData.setId(coreGroup.getId());
		coreGroupData.setTen(coreGroup.getTen());
		coreGroupData.setMa(coreGroup.getMa());
		coreGroupData.setMoTa(coreGroup.getMoTa());
		coreGroupData.setTrangThai(Boolean.TRUE.equals(coreGroup.getTrangThai()));
		coreGroupData.setSapXep(coreGroup.getSapXep());
		coreGroupData.setAppCode(coreGroup.getAppCode());
		return coreGroupData;
	}

	public CoreGroupData create(CoreGroupData coreGroupData) {
		CoreGroup coreGroup = new CoreGroup();
		return save(coreGroup, coreGroupData);
	}

	public void delete(Long id) throws EntityNotFoundException {
		Optional<CoreGroup> optional = coreGroupService.findById(id);
		if (optional.isEmpty()) {
			throw new EntityNotFoundException(CoreGroup.class, id);
		}
		CoreGroup coreGroup = optional.get();
		coreGroup.setDaXoa(true);
		coreGroupService.save(coreGroup);
	}

	public Page<CoreGroupData> findAll(int page, int size, String sortBy, String sortDir, String search, Boolean trangThai, String appCode) {
		Pageable pageable = CoreUtils.getPageRequest(page, size, sortBy, sortDir);
		Page<CoreGroup> pageCoreGroup = coreGroupService.findAll(search, trangThai, appCode, pageable);
		return pageCoreGroup.map(this::convertToCoreGroupData);
	}

	public CoreGroupData findById(Long id) throws EntityNotFoundException {
		Optional<CoreGroup> optional = coreGroupService.findById(id);
		if (optional.isEmpty()) {
			throw new EntityNotFoundException(CoreGroup.class, id);
		}
		return convertToCoreGroupData(optional.get());
	}

	private CoreGroupData save(CoreGroup coreGroup, CoreGroupData coreGroupData) {
		coreGroup.setDaXoa(false);
		coreGroup.setTen(FunctionUtils.removeXss(coreGroupData.getTen()));
		coreGroup.setMa(FunctionUtils.removeXss(coreGroupData.getMa()));
		coreGroup.setChaId(coreGroupData.getChaId());
		coreGroup.setMoTa(FunctionUtils.removeXss(coreGroupData.getMoTa()));
		coreGroup.setSapXep(coreGroupData.getSapXep());
		coreGroup.setTrangThai(Boolean.TRUE.equals(coreGroupData.getTrangThai()));
		coreGroup.setAppCode(FunctionUtils.removeXss(coreGroupData.getAppCode()));
		coreGroup = coreGroupService.save(coreGroup);
		return convertToCoreGroupData(coreGroup);
	}

	public CoreGroupData update(Long id, CoreGroupData coreGroupData) throws EntityNotFoundException {
		Optional<CoreGroup> optional = coreGroupService.findById(id);
		if (optional.isEmpty()) {
			throw new EntityNotFoundException(CoreGroup.class, id);
		}
		CoreGroup coreGroup = optional.get();
		return save(coreGroup, coreGroupData);
	}

}
