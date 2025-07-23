package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.tr.core.dao.model.CoreTag;
import vn.tr.core.data.criteria.CoreTagSearchCriteria;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CoreTagService {
	
	Optional<CoreTag> findById(Long id);
	
	List<CoreTag> findAllByIds(Collection<Long> ids);
	
	CoreTag save(CoreTag coreTag);
	
	void delete(Long id);
	
	void deleteByIds(Collection<Long> ids);
	
	Page<CoreTag> findAll(CoreTagSearchCriteria criteria, Pageable pageable);
	
	List<CoreTag> findAll(CoreTagSearchCriteria criteria);
	
	Optional<CoreTag> findByCodeIgnoreCaseIncludingDeleted(String code);
	
	JpaRepository<CoreTag, Long> getRepository();
	
	boolean existsByIdNotAndCodeIgnoreCase(long id, String code);
	
	boolean existsByIdNotAndNameIgnoreCase(long id, String name);
	
	boolean existsByCodeIgnoreCase(String code);
	
	boolean existsByNameIgnoreCase(String name);
	
	boolean existsById(Long id);
	
}
