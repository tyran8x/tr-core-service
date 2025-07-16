package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreApp;
import vn.tr.core.data.criteria.CoreAppSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CoreAppServiceImpl implements CoreAppService {
	
	private final CoreAppRepo repo;
	
	public CoreAppServiceImpl(CoreAppRepo repo) {
		this.repo = repo;
	}
	
	@Override
	public Optional<CoreApp> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public CoreApp save(CoreApp coreApp) {
		return repo.save(coreApp);
	}
	
	@Override
	public void delete(Long id) {
		repo.deleteById(id);
	}
	
	@Override
	public Page<CoreApp> findAll(CoreAppSearchCriteria coreAppSearchCriteria, Pageable pageable) {
		return repo.findAll(CoreAppSpecifications.quickSearch(coreAppSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreApp> findAll(CoreAppSearchCriteria coreAppSearchCriteria) {
		return repo.findAll(CoreAppSpecifications.quickSearch(coreAppSearchCriteria));
	}
	
	@Override
	public boolean existsByIdNotAndCodeIgnoreCase(long id, String code) {
		return repo.existsByIdNotAndCodeIgnoreCase(id, code);
	}
	
	@Override
	public boolean existsByIdNotAndNameIgnoreCase(long id, String name) {
		return repo.existsByIdNotAndNameIgnoreCase(id, name);
	}
	
	@Override
	public boolean existsByCodeIgnoreCase(String code) {
		return repo.existsByCodeIgnoreCase(code);
	}
	
	@Override
	public boolean existsByNameIgnoreCase(String name) {
		return repo.existsByNameIgnoreCase(name);
	}
	
	@Override
	public boolean existsById(long id) {
		return repo.existsById(id);
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
