package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.tr.core.dao.model.CoreGroup;
import vn.tr.core.data.criteria.CoreGroupSearchCriteria;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
	public Page<CoreGroup> findAll(CoreGroupSearchCriteria criteria, Pageable pageable) {
		return repo.findAll(CoreGroupSpecifications.quickSearch(criteria), pageable);
	}
	
	@Override
	public boolean existsByIdNotAndCodeIgnoreCaseAndAppId(long id, String code, Long appId) {
		return repo.existsByIdNotAndCodeIgnoreCaseAndAppId(id, code, appId);
	}
	
	@Override
	public boolean existsByIdNotAndNameIgnoreCaseAndAppId(long id, String name, Long appId) {
		return repo.existsByIdNotAndNameIgnoreCaseAndAppId(id, name, appId);
	}
	
	@Override
	public boolean existsByCodeIgnoreCaseAndAppId(String code, Long appId) {
		return repo.existsByCodeIgnoreCaseAndAppId(code, appId);
	}
	
	@Override
	public boolean existsByNameIgnoreCaseAndAppId(String name, Long appId) {
		return repo.existsByNameIgnoreCaseAndAppId(name, appId);
	}
	
	@Override
	public Optional<CoreGroup> findFirstByCodeIgnoreCase(String code) {
		return repo.findFirstByCodeIgnoreCase(code);
	}
	
	@Override
	public List<CoreGroup> findByCodeInIgnoreCase(Set<String> codes) {
		return repo.findByCodeInIgnoreCase(codes);
	}
	
	@Override
	public List<CoreGroup> findByStatusTrue() {
		return repo.findByStatusTrue();
	}
	
	@Override
	public boolean existsByIdAndAppId(long id, Long appId) {
		return repo.existsByIdAndAppId(id, appId);
	}
	
	@Override
	public List<CoreGroup> findByIdIn(Set<Long> ids) {
		return repo.findByIdIn(ids);
	}
	
	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}
	
	@Override
	public Map<Long, CoreGroup> findMapByIds(Set<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyMap();
		}
		return repo.findByIdIn(ids).stream()
				.collect(Collectors.toMap(CoreGroup::getId, Function.identity()));
	}
	
}
