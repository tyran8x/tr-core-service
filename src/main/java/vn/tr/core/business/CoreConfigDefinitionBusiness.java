package vn.tr.core.business;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.core.dao.model.CoreConfigDefinition;
import vn.tr.core.dao.service.CoreConfigDefinitionService;
import vn.tr.core.data.CoreConfigDefinitionData;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoreConfigDefinitionBusiness {
	
	private final CoreConfigDefinitionService coreConfigDefinitionService;
	
	public CoreConfigDefinitionData create(CoreConfigDefinitionData coreConfigDefinitionData) {
		CoreConfigDefinition coreConfigDefinition = new CoreConfigDefinition();
		return save(coreConfigDefinition, coreConfigDefinitionData);
	}
	
	private CoreConfigDefinitionData save(CoreConfigDefinition coreConfigDefinition, CoreConfigDefinitionData coreConfigDefinitionData) {
		coreConfigDefinition.setDaXoa(false);
//		coreConfigDefinition.setMaUngDung(FunctionUtils.removeXss(coreConfigDefinitionData.getMaUngDung()));
//		coreConfigDefinition.setCode(FunctionUtils.removeXss(coreConfigDefinitionData.getCode()));
//		coreConfigDefinition.setGhiChu(FunctionUtils.removeXss(coreConfigDefinitionData.getGhiChu()));
//		coreConfigDefinition.setLoaiGiaTri(coreConfigDefinitionData.getLoaiGiaTri());
//		coreConfigDefinition.setGiaTri(FunctionUtils.removeXss(coreConfigDefinitionData.getGiaTri()));
//		coreConfigDefinition.setTrangThai(coreConfigDefinitionData.getTrangThai());
		return null;// convertToCoreConfigDefinitionData(coreConfigDefinitionService.save(coreConfigDefinition));
	}
	
	public void delete(Long id) throws EntityNotFoundException {
		Optional<CoreConfigDefinition> optional = coreConfigDefinitionService.findById(id);
		if (optional.isEmpty()) {
			throw new EntityNotFoundException(CoreConfigDefinition.class, id);
		}
		CoreConfigDefinition coreConfigDefinition = optional.get();
		coreConfigDefinition.setDaXoa(true);
		//coreConfigDefinitionService.save(coreConfigDefinition);
	}
	
	public void deleteByIds(List<Long> ids) {
		if (CollUtil.isNotEmpty(ids)) {
			//	coreConfigDefinitionService.setFixedDaXoaForIds(true, ids);
		}
	}
	
	public Page<CoreConfigDefinitionData> findAll(int page, int size, String sortBy, String sortDir, String maUngDung, String code,
			Boolean trangThai) {
		Pageable pageable = CoreUtils.getPageRequest(page, size, sortBy, sortDir);
		Page<CoreConfigDefinition> pageCoreConfigDefinition = coreConfigDefinitionService.findAll(maUngDung, code, trangThai, pageable);
		return pageCoreConfigDefinition.map(this::convertToCoreConfigDefinitionData);
	}
	
	private CoreConfigDefinitionData convertToCoreConfigDefinitionData(CoreConfigDefinition coreConfigDefinition) {
		CoreConfigDefinitionData coreConfigDefinitionData = new CoreConfigDefinitionData();
		coreConfigDefinitionData.setId(coreConfigDefinition.getId());
//		coreConfigDefinitionData.setMaUngDung(coreConfigDefinition.getMaUngDung());
//		coreConfigDefinitionData.setCode(coreConfigDefinition.getCode());
//		coreConfigDefinitionData.setGhiChu(coreConfigDefinition.getGhiChu());
//		coreConfigDefinitionData.setLoaiGiaTri(coreConfigDefinition.getLoaiGiaTri());
//		coreConfigDefinitionData.setGiaTri(coreConfigDefinition.getGiaTri());
//		coreConfigDefinitionData.setTrangThai(coreConfigDefinition.getTrangThai());
		return coreConfigDefinitionData;
	}
	
	public CoreConfigDefinitionData findById(Long id) throws EntityNotFoundException {
		Optional<CoreConfigDefinition> optionalCoreConfigDefinition = coreConfigDefinitionService.findById(id);
		if (optionalCoreConfigDefinition.isEmpty()) {
			throw new EntityNotFoundException(CoreConfigDefinition.class, id);
		}
		return convertToCoreConfigDefinitionData(optionalCoreConfigDefinition.get());
	}
	
	public void saveConfig(String code, String giaTri, String maUngDung) {
		CoreConfigDefinition coreConfigDefinition = new CoreConfigDefinition();
//		Optional<CoreConfigDefinition> optionalCoreConfigDefinition = coreConfigDefinitionService.findFirstByCodeAndDaXoaFalse(code);
//		if (optionalCoreConfigDefinition.isPresent()) {
//			coreConfigDefinition = optionalCoreConfigDefinition.get();
//		}
		coreConfigDefinition.setDaXoa(false);
//		coreConfigDefinition.setCode(code);
//		coreConfigDefinition.setGiaTri(giaTri);
//		coreConfigDefinition.setMaUngDung(maUngDung);
//		coreConfigDefinitionService.save(coreConfigDefinition);
	}
	
	public CoreConfigDefinitionData update(Long id, CoreConfigDefinitionData coreConfigDefinitionData) throws EntityNotFoundException {
		Optional<CoreConfigDefinition> optionalCoreConfigDefinition = coreConfigDefinitionService.findById(id);
		if (optionalCoreConfigDefinition.isEmpty()) {
			throw new EntityNotFoundException(CoreConfigDefinition.class, id);
		}
		CoreConfigDefinition coreConfigDefinition = optionalCoreConfigDefinition.get();
		return save(coreConfigDefinition, coreConfigDefinitionData);
	}
	
}
