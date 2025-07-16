package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tr.core.dao.model.CoreGroup;
import vn.tr.core.data.criteria.CoreGroupSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CoreGroupService {
	
	Optional<CoreGroup> findById(Long id);
	
	CoreGroup save(CoreGroup coreGroup);
	
	void delete(Long id);
	
	boolean existsById(Long id);
	
	Page<CoreGroup> findAll(CoreGroupSearchCriteria coreGroupSearchCriteria, Pageable pageable);
	
	List<CoreGroup> findAll(CoreGroupSearchCriteria coreGroupSearchCriteria);
	
	boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode);
	
	boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode);
	
	boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode);
	
	boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode);
	
	boolean existsByIdAndAppCode(long id, String appCode);
	
	void deleteByIds(Set<Long> ids);
	
}
