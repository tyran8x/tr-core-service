package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tr.core.dao.model.CoreTag;
import vn.tr.core.data.criteria.CoreTagSearchCriteria;
import vn.tr.core.data.dto.CoreTagAssignmentData;
import vn.tr.core.data.dto.CoreTagData;

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
	
	boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode);
	
	boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode);
	
	boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode);
	
	boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode);
	
	boolean existsByIdAndAppCode(long id, String appCode);
	
	boolean existsById(long id);
	
	void deleteByIds(Set<Long> ids);
	
	void assignTag(String taggableValue, String taggableType, String tagCode);
	
	void synchronizeTagsForTaggable(String taggableType, String taggableValue, List<CoreTagAssignmentData> newAssignments);
	
	CoreTag upsert(CoreTagData coreTagData);
	
}
