package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tr.core.dao.model.CoreTag;
import vn.tr.core.data.criteria.CoreTagSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CoreTagService {
	
	Optional<CoreTag> findById(Long id);
	
	CoreTag save(CoreTag coreTag);
	
	void delete(Long id);
	
	boolean existsById(Long id);
	
	Page<CoreTag> findAll(CoreTagSearchCriteria coreTagSearchCriteria, Pageable pageable);
	
	List<CoreTag> findAll(CoreTagSearchCriteria coreTagSearchCriteria);
	
	boolean existsByIdNotAndCodeIgnoreCase(long id, String code);
	
	boolean existsByIdNotAndNameIgnoreCase(long id, String name);
	
	boolean existsByCodeIgnoreCase(String code);
	
	boolean existsByNameIgnoreCase(String name);
	
	boolean existsById(long id);
	
	void deleteByIds(Set<Long> ids);
	
	void assignTag(String taggableValue, String taggableType, String tagCode);
	
	boolean hasTag(String taggableValue, String taggableType, String tagCode);
	
}
