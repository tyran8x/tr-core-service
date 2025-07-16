package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tr.core.dao.model.CoreModule;
import vn.tr.core.data.criteria.CoreModuleSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CoreModuleService {
	
	Optional<CoreModule> findById(Long id);
	
	CoreModule save(CoreModule coreModule);
	
	void delete(Long id);
	
	boolean existsById(Long id);
	
	Page<CoreModule> findAll(CoreModuleSearchCriteria coreModuleSearchCriteria, Pageable pageable);
	
	List<CoreModule> findAll(CoreModuleSearchCriteria coreModuleSearchCriteria);
	
	boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode);
	
	boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode);
	
	boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode);
	
	boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode);
	
	boolean existsByIdAndAppCode(long id, String appCode);
	
	void deleteByIds(Set<Long> ids);
	
}
