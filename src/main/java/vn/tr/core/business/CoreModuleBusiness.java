package vn.tr.core.business;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.common.web.utils.PagedResult;
import vn.tr.core.dao.model.CoreModule;
import vn.tr.core.dao.service.CoreModuleService;
import vn.tr.core.data.criteria.CoreModuleSearchCriteria;
import vn.tr.core.data.dto.CoreModuleData;
import vn.tr.core.data.mapper.CoreModuleMapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CoreModuleBusiness {
	
	private final CoreModuleService coreModuleService;
	private final CoreModuleMapper coreModuleMapper;
	
	public CoreModuleData create(CoreModuleData coreModuleData) {
		CoreModule coreModule = coreModuleMapper.toEntity(coreModuleData);
		coreModule.setAppCode(LoginHelper.getAppCode());
		return save(coreModule, coreModuleData);
	}
	
	private CoreModuleData save(CoreModule coreModule, CoreModuleData coreModuleData) {
		coreModuleMapper.updateEntityFromData(coreModuleData, coreModule);
		CoreModule savedModule = coreModuleService.save(coreModule);
		return findById(savedModule.getId());
	}
	
	@Transactional(readOnly = true)
	public CoreModuleData findById(Long id) {
		return coreModuleService.findById(id)
				.map(coreModuleMapper::toData)
				.orElseThrow(() -> new EntityNotFoundException(CoreModule.class, id));
	}
	
	public void delete(Long id) {
		if (!coreModuleService.existsById(id)) {
			throw new EntityNotFoundException(CoreModule.class, id);
		}
		coreModuleService.delete(id);
	}
	
	public void bulkDelete(Set<Long> ids) {
		coreModuleService.deleteByIds(ids);
	}
	
	@Transactional(readOnly = true)
	public PagedResult<CoreModuleData> findAll(CoreModuleSearchCriteria criteria) {
		Pageable pageable = CoreUtils.getPageRequest(criteria.getPage(), criteria.getSize(), criteria.getSortBy(), criteria.getSortDir());
		Page<CoreModule> pageCoreModule = coreModuleService.findAll(criteria, pageable);
		return PagedResult.from(pageCoreModule, coreModuleMapper::toData);
	}
	
	@Transactional(readOnly = true)
	public List<CoreModuleData> getAll(CoreModuleSearchCriteria criteria) {
		List<CoreModule> pageCoreModule = coreModuleService.findAll(criteria);
		return pageCoreModule.stream().map(coreModuleMapper::toData).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public Optional<CoreModuleData> getById(Long id) {
		if (id == null) {
			return Optional.empty();
		}
		return coreModuleService.findById(id).map(coreModuleMapper::toData);
	}
	
	public CoreModuleData update(Long id, CoreModuleData coreModuleData) {
		CoreModule coreModule = coreModuleService.findById(id).orElseThrow(() -> new EntityNotFoundException(CoreModule.class, id));
		return save(coreModule, coreModuleData);
	}
}
