package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.tr.core.dao.model.CoreUserType;
import vn.tr.core.data.criteria.CoreUserTypeSearchCriteria;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CoreUserTypeService {
	
	Optional<CoreUserType> findById(Long id);
	
	CoreUserType save(CoreUserType coreUserType);
	
	void delete(Long id);
	
	boolean existsById(Long id);
	
	Page<CoreUserType> findAll(CoreUserTypeSearchCriteria coreUserTypeSearchCriteria, Pageable pageable);
	
	List<CoreUserType> findAll(CoreUserTypeSearchCriteria coreUserTypeSearchCriteria);
	
	boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode);
	
	boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode);
	
	boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode);
	
	boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode);
	
	boolean existsByIdAndAppCode(long id, String appCode);
	
	List<CoreUserType> findAllByIds(Collection<Long> ids);
	
	void deleteByIds(Collection<Long> ids);
	
	Optional<CoreUserType> findByCodeAndAppCodeIncludingDeleted(String code, String appCode);
	
	JpaRepository<CoreUserType, Long> getRepository();
	
}
