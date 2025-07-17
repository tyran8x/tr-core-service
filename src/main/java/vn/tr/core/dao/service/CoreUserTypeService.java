package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tr.core.dao.model.CoreUserType;
import vn.tr.core.data.criteria.CoreUserTypeSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CoreUserTypeService {
	
	Optional<CoreUserType> findById(Long id);
	
	CoreUserType save(CoreUserType coreUserType);
	
	void delete(Long id);
	
	boolean existsById(Long id);
	
	Page<CoreUserType> findAll(CoreUserTypeSearchCriteria coreUserTypeSearchCriteria, Pageable pageable);
	
	List<CoreUserType> findAll(CoreUserTypeSearchCriteria coreUserTypeSearchCriteria);
	
	boolean existsByIdNotAndCodeIgnoreCase(long id, String code);
	
	boolean existsByIdNotAndNameIgnoreCase(long id, String name);
	
	boolean existsByCodeIgnoreCase(String code);
	
	boolean existsByNameIgnoreCase(String name);
	
	boolean existsById(long id);
	
	void deleteByIds(Set<Long> ids);
	
	CoreUserType findOrCreate(String code, String name);
	
}
