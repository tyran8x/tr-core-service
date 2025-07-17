package vn.tr.core.business;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.core.dao.model.CoreApp;
import vn.tr.core.dao.service.CoreAppService;
import vn.tr.core.data.criteria.CoreAppSearchCriteria;
import vn.tr.core.data.dto.CoreAppData;
import vn.tr.core.data.mapper.CoreAppMapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CoreAppBusiness {
	
	private final CoreAppService coreAppService;
	private final CoreAppMapper coreAppMapper;
	
	public CoreAppData create(CoreAppData coreAppData) {
		CoreApp coreApp = coreAppMapper.toEntity(coreAppData);
		return save(coreApp, coreAppData);
	}
	
	private CoreAppData save(CoreApp coreApp, CoreAppData coreAppData) {
		coreAppMapper.save(coreAppData, coreApp);
		CoreApp savedApp = coreAppService.save(coreApp);
		return findById(savedApp.getId());
	}
	
	@Transactional(readOnly = true)
	public CoreAppData findById(Long id) {
		return coreAppService.findById(id)
				.map(coreAppMapper::toData)
				.orElseThrow(() -> new EntityNotFoundException(CoreApp.class, id));
	}
	
	public void delete(Long id) {
		if (!coreAppService.existsById(id)) {
			throw new EntityNotFoundException(CoreApp.class, id);
		}
		coreAppService.delete(id);
	}
	
	public void bulkDelete(Set<Long> ids) {
		coreAppService.deleteByIds(ids);
	}
	
	@Transactional(readOnly = true)
	public Page<CoreAppData> findAll(CoreAppSearchCriteria criteria) {
		Pageable pageable = CoreUtils.getPageRequest(criteria.getPage(), criteria.getSize(), criteria.getSortBy(), criteria.getSortDir());
		Page<CoreApp> pageCoreApp = coreAppService.findAll(criteria, pageable);
		return pageCoreApp.map(coreAppMapper::toData);
	}
	
	@Transactional(readOnly = true)
	public List<CoreAppData> getAll(CoreAppSearchCriteria criteria) {
		List<CoreApp> pageCoreApp = coreAppService.findAll(criteria);
		return pageCoreApp.stream().map(coreAppMapper::toData).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public Optional<CoreAppData> getById(Long id) {
		if (id == null) {
			return Optional.empty();
		}
		return coreAppService.findById(id).map(coreAppMapper::toData);
	}
	
	public CoreAppData update(Long id, CoreAppData coreAppData) {
		CoreApp coreApp = coreAppService.findById(id).orElseThrow(() -> new EntityNotFoundException(CoreApp.class, id));
		return save(coreApp, coreAppData);
	}
}
