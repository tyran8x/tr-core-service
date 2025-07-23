package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.tr.core.dao.model.CoreApp;
import vn.tr.core.data.criteria.CoreAppSearchCriteria;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CoreAppService {
	
	Optional<CoreApp> findById(Long id);
	
	List<CoreApp> findAllByIds(Collection<Long> ids);
	
	CoreApp save(CoreApp coreApp);
	
	void delete(Long id);
	
	void deleteByIds(Collection<Long> ids);
	
	Page<CoreApp> findAll(CoreAppSearchCriteria criteria, Pageable pageable);
	
	List<CoreApp> findAll(CoreAppSearchCriteria criteria);
	
	Optional<CoreApp> findByCodeIgnoreCaseIncludingDeleted(String code);
	
	JpaRepository<CoreApp, Long> getRepository();
	
	boolean existsByIdNotAndCodeIgnoreCase(long id, String code);
	
	boolean existsByIdNotAndNameIgnoreCase(long id, String name);
	
	boolean existsByCodeIgnoreCase(String code);
	
	boolean existsByNameIgnoreCase(String name);
	
	boolean existsById(Long id);
	
}
