package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tr.core.dao.model.CoreGroup;
import vn.tr.core.data.criteria.CoreGroupSearchCriteria;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface CoreGroupService {
	
	Optional<CoreGroup> findById(Long id);
	
	CoreGroup save(CoreGroup coreGroup);
	
	void delete(Long id);
	
	boolean existsById(Long id);
	
	Page<CoreGroup> findAll(CoreGroupSearchCriteria coreGroupSearchCriteria, Pageable pageable);
	
	boolean existsByIdNotAndCodeIgnoreCaseAndAppId(long id, String code, Long appId);
	
	boolean existsByIdNotAndNameIgnoreCaseAndAppId(long id, String name, Long appId);
	
	boolean existsByCodeIgnoreCaseAndAppId(String code, Long appId);
	
	boolean existsByNameIgnoreCaseAndAppId(String name, Long appId);
	
	Optional<CoreGroup> findFirstByCodeIgnoreCase(String code);
	
	List<CoreGroup> findByCodeInIgnoreCase(Set<String> codes);
	
	List<CoreGroup> findByStatusTrue();
	
	boolean existsByIdAndAppId(long id, Long appId);
	
	List<CoreGroup> findByIdIn(Set<Long> ids);
	
	Map<Long, CoreGroup> findMapByIds(Set<Long> ids);
	
}
