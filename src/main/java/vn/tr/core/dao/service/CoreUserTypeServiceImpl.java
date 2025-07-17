package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreUserType;
import vn.tr.core.data.criteria.CoreUserTypeSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CoreUserTypeServiceImpl implements CoreUserTypeService {
	
	private final CoreUserTypeRepo repo;
	
	public CoreUserTypeServiceImpl(CoreUserTypeRepo repo) {
		this.repo = repo;
	}
	
	@Override
	public Optional<CoreUserType> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public CoreUserType save(CoreUserType coreUserType) {
		return repo.save(coreUserType);
	}
	
	@Override
	public void delete(Long id) {
		repo.deleteById(id);
	}
	
	@Override
	public Page<CoreUserType> findAll(CoreUserTypeSearchCriteria coreUserTypeSearchCriteria, Pageable pageable) {
		return repo.findAll(CoreUserTypeSpecifications.quickSearch(coreUserTypeSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreUserType> findAll(CoreUserTypeSearchCriteria coreUserTypeSearchCriteria) {
		return repo.findAll(CoreUserTypeSpecifications.quickSearch(coreUserTypeSearchCriteria));
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
