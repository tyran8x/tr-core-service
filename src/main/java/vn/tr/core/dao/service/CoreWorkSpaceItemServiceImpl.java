package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CoreWorkSpaceItemServiceImpl implements CoreWorkSpaceItemService {
	
	private final CoreWorkSpaceItemRepo coreWorkSpaceItemRepo;
	
	@Override
	public Optional<CoreWorkSpaceItem> findById(Long id) {
		return coreWorkSpaceItemRepo.findById(id);
	}
	
	@Override
	public CoreWorkSpaceItem save(CoreWorkSpaceItem coreWorkSpaceItem) {
		return coreWorkSpaceItemRepo.save(coreWorkSpaceItem);
	}
	
	@Override
	public void delete(Long id) {
		coreWorkSpaceItemRepo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return coreWorkSpaceItemRepo.existsById(id);
	}
	
	@Override
	public Page<CoreWorkSpaceItem> findAll(CoreWorkSpaceItemSearchCriteria coreWorkSpaceItemSearchCriteria, Pageable pageable) {
		return coreWorkSpaceItemRepo.findAll(CoreWorkSpaceItemSpecifications.quickSearch(coreWorkSpaceItemSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreWorkSpaceItem> findAll(CoreWorkSpaceItemSearchCriteria coreWorkSpaceItemSearchCriteria) {
		return coreWorkSpaceItemRepo.findAll(CoreWorkSpaceItemSpecifications.quickSearch(coreWorkSpaceItemSearchCriteria));
	}
	
	@Override
	public boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode) {
		return coreWorkSpaceItemRepo.existsByIdNotAndCodeIgnoreCaseAndAppCode(id, code, appCode);
	}
	
	@Override
	public boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode) {
		return coreWorkSpaceItemRepo.existsByIdNotAndNameIgnoreCaseAndAppCode(id, name, appCode);
	}
	
	@Override
	public boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode) {
		return coreWorkSpaceItemRepo.existsByCodeIgnoreCaseAndAppCode(code, appCode);
	}
	
	@Override
	public boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode) {
		return coreWorkSpaceItemRepo.existsByNameIgnoreCaseAndAppCode(name, appCode);
	}
	
	@Override
	public boolean existsByIdAndAppCode(long id, String appCode) {
		return coreWorkSpaceItemRepo.existsByIdAndAppCode(id, appCode);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Set<Long> ids) {
		if (ids.isEmpty()) {
			return;
		}
		coreWorkSpaceItemRepo.softDeleteByIds(ids);
	}
	
}
