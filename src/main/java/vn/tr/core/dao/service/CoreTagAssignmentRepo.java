package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreTagAssignment;

import java.util.List;
import java.util.Set;

@Repository
public interface CoreTagAssignmentRepo extends JpaRepository<CoreTagAssignment, Long>, JpaSpecificationExecutor<CoreTagAssignment> {
	
	@Query("SELECT cta.tagCode FROM CoreTagAssignment cta WHERE cta.taggableType = :type AND cta.taggableValue = :value")
	Set<String> findActiveTagsForTaggable(@Param("type") String taggableType, @Param("value") String taggableValue);
	
	boolean existsByTagCodeAndTaggableValueAndTaggableType(String tagCode, String taggableValue, String taggableType);
	
	@Query("SELECT cta FROM CoreTagAssignment cta WHERE cta.taggableType = :type AND cta.taggableValue = :value AND cta.tagCode = :tagCode")
	List<CoreTagAssignment> findAllByTaggableAndTagCodeIncludingDeleted(@Param("type") String taggableType, @Param("value") String taggableValue,
			@Param("tagCode") String tagCode);
	
	/**
	 * Tìm tất cả các bản ghi gán thẻ cho một đối tượng cụ thể, BAO GỒM CẢ ĐÃ BỊ XÓA MỀM.
	 * Cần thiết cho logic đồng bộ hóa (AssociationSyncHelper).
	 */
	@Query("SELECT ta FROM CoreTagAssignment ta WHERE ta.taggableType = :type AND ta.taggableValue = :value")
	List<CoreTagAssignment> findAllByTaggableIncludingDeleted(@Param("type") String taggableType, @Param("value") String taggableValue);
	
	/**
	 * Kiểm tra xem một thẻ tag (dựa trên code) có đang được sử dụng ở bất kỳ đâu không.
	 *
	 * @param tagCode Mã của thẻ tag.
	 *
	 * @return true nếu đang được sử dụng, ngược lại false.
	 */
	boolean existsByTagCode(String tagCode);
	
}
