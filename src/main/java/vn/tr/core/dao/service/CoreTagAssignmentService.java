package vn.tr.core.dao.service;

import vn.tr.core.dao.model.CoreTagAssignment;

import java.util.Optional;

public interface CoreTagAssignmentService {
	
	void deleteById(Long id);
	
	boolean existsById(Long id);
	
	Optional<CoreTagAssignment> findById(Long id);
	
	CoreTagAssignment save(CoreTagAssignment coreTagAssignment);
	
	Optional<CoreTagAssignment> findFirstByTagIdAndTaggableValueAndTaggableType(Long tagId, String taggableValue, String taggableType);
	
}
