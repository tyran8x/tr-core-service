package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CoreGroupServiceImpl implements CoreGroupService {
	
	private final CoreGroupRepo coreGroupRepo;
	
	@Override
	public Optional<CoreGroup> findById(Long id) {
		return coreGroupRepo.findById(id);
	}
	
	@Override
	public CoreGroup save(CoreGroup coreGroup) {
		return coreGroupRepo.save(coreGroup);
	}
	
	@Override
	public void delete(Long id) {
		coreGroupRepo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return coreGroupRepo.existsById(id);
	}
	
	@Override
	public Page<CoreGroup> findAll(CoreGroupSearchCriteria coreGroupSearchCriteria, Pageable pageable) {
		return coreGroupRepo.findAll(CoreGroupSpecifications.quickSearch(coreGroupSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreGroup> findAll(CoreGroupSearchCriteria coreGroupSearchCriteria) {
		return coreGroupRepo.findAll(CoreGroupSpecifications.quickSearch(coreGroupSearchCriteria));
	}
	
	@Override
	public boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode) {
		return coreGroupRepo.existsByIdNotAndCodeIgnoreCaseAndAppCode(id, code, appCode);
	}
	
	@Override
	public boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode) {
		return coreGroupRepo.existsByIdNotAndNameIgnoreCaseAndAppCode(id, name, appCode);
	}
	
	@Override
	public boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode) {
		return coreGroupRepo.existsByCodeIgnoreCaseAndAppCode(code, appCode);
	}
	
	@Override
	public boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode) {
		return coreGroupRepo.existsByNameIgnoreCaseAndAppCode(name, appCode);
	}
	
	@Override
	public boolean existsByIdAndAppCode(long id, String appCode) {
		return coreGroupRepo.existsByIdAndAppCode(id, appCode);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Set<Long> ids) {
		if (ids.isEmpty()) {
			return;
		}
		coreGroupRepo.softDeleteByIds(ids);
	}
	
}
