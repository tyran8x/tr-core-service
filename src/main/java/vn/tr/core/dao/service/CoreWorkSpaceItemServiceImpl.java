package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreWorkSpaceItem;
import vn.tr.core.data.criteria.CoreWorkSpaceItemSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CoreWorkSpaceItemServiceImpl implements CoreWorkSpaceItemService {
	
	private final CoreWorkSpaceItemRepo repo;
	
	public CoreWorkSpaceItemServiceImpl(CoreWorkSpaceItemRepo repo) {
		this.repo = repo;
	}
	
	@Override
	public Optional<CoreWorkSpaceItem> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public CoreWorkSpaceItem save(CoreWorkSpaceItem coreWorkSpaceItem) {
		return repo.save(coreWorkSpaceItem);
	}
	
	@Override
	public void delete(Long id) {
		repo.deleteById(id);
	}
	
	@Override
	public Page<CoreWorkSpaceItem> findAll(CoreWorkSpaceItemSearchCriteria coreWorkSpaceItemSearchCriteria, Pageable pageable) {
		return repo.findAll(CoreWorkSpaceItemSpecifications.quickSearch(coreWorkSpaceItemSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreWorkSpaceItem> findAll(CoreWorkSpaceItemSearchCriteria coreWorkSpaceItemSearchCriteria) {
		return repo.findAll(CoreWorkSpaceItemSpecifications.quickSearch(coreWorkSpaceItemSearchCriteria));
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
