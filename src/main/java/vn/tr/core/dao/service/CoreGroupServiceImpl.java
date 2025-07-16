package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreGroup;
import vn.tr.core.data.criteria.CoreGroupSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CoreGroupServiceImpl implements CoreGroupService {
	
	private final CoreGroupRepo repo;
	
	public CoreGroupServiceImpl(CoreGroupRepo repo) {
		this.repo = repo;
	}
	
	@Override
	public Optional<CoreGroup> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public CoreGroup save(CoreGroup coreGroup) {
		return repo.save(coreGroup);
	}
	
	@Override
	public void delete(Long id) {
		repo.deleteById(id);
	}
	
	@Override
	public Page<CoreGroup> findAll(CoreGroupSearchCriteria coreGroupSearchCriteria, Pageable pageable) {
		return repo.findAll(CoreGroupSpecifications.quickSearch(coreGroupSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreGroup> findAll(CoreGroupSearchCriteria coreGroupSearchCriteria) {
		return repo.findAll(CoreGroupSpecifications.quickSearch(coreGroupSearchCriteria));
	}
	
	@Override
	public boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode) {
		return repo.existsByIdNotAndCodeIgnoreCaseAndAppCode(id, code, appCode);
	}
	
	@Override
	public boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode) {
		return repo.existsByIdNotAndNameIgnoreCaseAndAppCode(id, name, appCode);
	}
	
	@Override
	public boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode) {
		return repo.existsByCodeIgnoreCaseAndAppCode(code, appCode);
	}
	
	@Override
	public boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode) {
		return repo.existsByNameIgnoreCaseAndAppCode(name, appCode);
	}
	
	@Override
	public boolean existsByIdAndAppCode(long id, String appCode) {
		return repo.existsByIdAndAppCode(id, appCode);
	}
	
	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Set<Long> ids) {
		if (ids.isEmpty()) {
			return;
		}
		repo.softDeleteByIds(ids);
	}
	
}
