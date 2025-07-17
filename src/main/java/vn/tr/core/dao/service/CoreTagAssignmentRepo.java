package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreTagAssignment;

import java.util.Optional;

@Repository
public interface CoreTagAssignmentRepo extends JpaRepository<CoreTagAssignment, Long>, JpaSpecificationExecutor<CoreTagAssignment> {
	
	Optional<CoreTagAssignment> findFirstByTagIdAndTaggableValueAndTaggableType(Long tagId, String taggableValue, String taggableType);
	
	boolean existsByCoreTagCodeAndTaggableValueAndTaggableType(String tagCode, String taggableValue, String taggableType);
	
}
