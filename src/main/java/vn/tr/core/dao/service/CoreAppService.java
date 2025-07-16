package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tr.core.dao.model.CoreApp;
import vn.tr.core.data.criteria.CoreAppSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CoreAppService {
	
	Optional<CoreApp> findById(Long id);
	
	CoreApp save(CoreApp coreApp);
	
	void delete(Long id);
	
	boolean existsById(Long id);
	
	Page<CoreApp> findAll(CoreAppSearchCriteria coreAppSearchCriteria, Pageable pageable);
	
	List<CoreApp> findAll(CoreAppSearchCriteria coreAppSearchCriteria);
	
	boolean existsByIdNotAndCodeIgnoreCase(long id, String code);
	
	boolean existsByIdNotAndNameIgnoreCase(long id, String name);
	
	boolean existsByCodeIgnoreCase(String code);
	
	boolean existsByNameIgnoreCase(String name);
	
	boolean existsById(long id);
	
	void deleteByIds(Set<Long> ids);
	
}
