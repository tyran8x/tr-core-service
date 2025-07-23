package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.tr.core.dao.model.CoreTag;
import vn.tr.core.dao.model.CoreTagAssignment;

import java.util.Optional;
import java.util.Set;

public interface CoreTagAssignmentService {
	
	void deleteById(Long id);
	
	boolean existsById(Long id);
	
	Optional<CoreTagAssignment> findById(Long id);
	
	CoreTagAssignment save(CoreTagAssignment coreTagAssignment);
	
	void synchronizeTagsForTaggable(String taggableType, String taggableValue, Set<String> newTagCodes);
	
	/**
	 * Kiểm tra xem một CoreTag cụ thể có đang được sử dụng hay không.
	 *
	 * @param coreTag Đối tượng CoreTag cần kiểm tra.
	 *
	 * @return true nếu đang được sử dụng.
	 */
	boolean isTagInUse(CoreTag coreTag);
	
	JpaRepository<CoreTagAssignment, Long> getRepository();
	
}
