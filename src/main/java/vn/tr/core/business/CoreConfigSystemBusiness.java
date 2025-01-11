package vn.tr.core.business;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.core.utils.FunctionUtils;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.core.dao.model.CoreConfigSystem;
import vn.tr.core.dao.service.CoreConfigSystemService;
import vn.tr.core.data.CoreConfigSystemData;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoreConfigSystemBusiness {
	
	private final CoreConfigSystemService coreConfigSystemService;
	
	private CoreConfigSystemData convertToCoreConfigSystemData(CoreConfigSystem coreConfigSystem) {
		CoreConfigSystemData coreConfigSystemData = new CoreConfigSystemData();
		coreConfigSystemData.setId(coreConfigSystem.getId());
		coreConfigSystemData.setMaUngDung(coreConfigSystem.getMaUngDung());
		coreConfigSystemData.setCode(coreConfigSystem.getCode());
		coreConfigSystemData.setGhiChu(coreConfigSystem.getGhiChu());
		coreConfigSystemData.setLoaiGiaTri(coreConfigSystem.getLoaiGiaTri());
		coreConfigSystemData.setGiaTri(coreConfigSystem.getGiaTri());
		coreConfigSystemData.setTrangThai(coreConfigSystem.getTrangThai());
		return coreConfigSystemData;
	}
	
	public CoreConfigSystemData create(CoreConfigSystemData coreConfigSystemData) {
		CoreConfigSystem coreConfigSystem = new CoreConfigSystem();
		return save(coreConfigSystem, coreConfigSystemData);
	}
	
	public void delete(Long id) throws EntityNotFoundException {
		Optional<CoreConfigSystem> optional = coreConfigSystemService.findById(id);
		if (optional.isEmpty()) {
			throw new EntityNotFoundException(CoreConfigSystem.class, id);
		}
		CoreConfigSystem coreConfigSystem = optional.get();
		coreConfigSystem.setDaXoa(true);
		coreConfigSystemService.save(coreConfigSystem);
	}
	
	public void deleteByIds(List<Long> ids) {
		if (CollUtil.isNotEmpty(ids)) {
			coreConfigSystemService.setFixedDaXoaForIds(true, ids);
		}
	}
	
	public Page<CoreConfigSystemData> findAll(int page, int size, String sortBy, String sortDir, String maUngDung, String code, Boolean trangThai) {
		Pageable pageable = CoreUtils.getPageRequest(page, size, sortBy, sortDir);
		Page<CoreConfigSystem> pageCoreConfigSystem = coreConfigSystemService.findAll(maUngDung, code, trangThai, pageable);
		return pageCoreConfigSystem.map(this::convertToCoreConfigSystemData);
	}
	
	public CoreConfigSystemData findById(Long id) throws EntityNotFoundException {
		Optional<CoreConfigSystem> optionalCoreConfigSystem = coreConfigSystemService.findById(id);
		if (optionalCoreConfigSystem.isEmpty()) {
			throw new EntityNotFoundException(CoreConfigSystem.class, id);
		}
		return convertToCoreConfigSystemData(optionalCoreConfigSystem.get());
	}
	
	public List<CoreConfigSystemData> getAll(List<Long> ids) {
		if (CollUtil.isNotEmpty(ids)) {
			return coreConfigSystemService.findByIdInAndDaXoaFalse(ids).stream().map(this::convertToCoreConfigSystemData)
					.collect(Collectors.toList());
		}
		return coreConfigSystemService.findByDaXoaFalse().stream().map(this::convertToCoreConfigSystemData).collect(Collectors.toList());
	}
	
	public String getGiaTriByCode(String code) {
		return coreConfigSystemService.getGiaTriByCode(code);
	}
	
	public void saveConfig(String code, String giaTri, String maUngDung) {
		CoreConfigSystem coreConfigSystem = new CoreConfigSystem();
		Optional<CoreConfigSystem> optionalCoreConfigSystem = coreConfigSystemService.findFirstByCodeAndDaXoaFalse(code);
		if (optionalCoreConfigSystem.isPresent()) {
			coreConfigSystem = optionalCoreConfigSystem.get();
		}
		coreConfigSystem.setDaXoa(false);
		coreConfigSystem.setCode(code);
		coreConfigSystem.setGiaTri(giaTri);
		coreConfigSystem.setMaUngDung(maUngDung);
		coreConfigSystemService.save(coreConfigSystem);
	}
	
	private CoreConfigSystemData save(CoreConfigSystem coreConfigSystem, CoreConfigSystemData coreConfigSystemData) {
		coreConfigSystem.setDaXoa(false);
		coreConfigSystem.setMaUngDung(FunctionUtils.removeXss(coreConfigSystemData.getMaUngDung()));
		coreConfigSystem.setCode(FunctionUtils.removeXss(coreConfigSystemData.getCode()));
		coreConfigSystem.setGhiChu(FunctionUtils.removeXss(coreConfigSystemData.getGhiChu()));
		coreConfigSystem.setLoaiGiaTri(coreConfigSystemData.getLoaiGiaTri());
		coreConfigSystem.setGiaTri(FunctionUtils.removeXss(coreConfigSystemData.getGiaTri()));
		coreConfigSystem.setTrangThai(coreConfigSystemData.getTrangThai());
		return convertToCoreConfigSystemData(coreConfigSystemService.save(coreConfigSystem));
	}
	
	public CoreConfigSystemData update(Long id, CoreConfigSystemData coreConfigSystemData) throws EntityNotFoundException {
		Optional<CoreConfigSystem> optionalCoreConfigSystem = coreConfigSystemService.findById(id);
		if (optionalCoreConfigSystem.isEmpty()) {
			throw new EntityNotFoundException(CoreConfigSystem.class, id);
		}
		CoreConfigSystem coreConfigSystem = optionalCoreConfigSystem.get();
		return save(coreConfigSystem, coreConfigSystemData);
	}
	
}
