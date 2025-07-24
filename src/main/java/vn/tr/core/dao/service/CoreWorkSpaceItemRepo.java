package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreWorkSpaceItem;

import java.util.List;

/**
 * Repository interface cho thực thể CoreWorkSpaceItem.
 *
 * @author tyran8x
 * @version 2.0
 */
@Repository
public interface CoreWorkSpaceItemRepo extends JpaRepository<CoreWorkSpaceItem, Long>, JpaSpecificationExecutor<CoreWorkSpaceItem> {
	
	/**
	 * Tìm tất cả các item của một chủ sở hữu cụ thể trong một ứng dụng,
	 * BAO GỒM CẢ CÁC BẢN GHI ĐÃ BỊ XÓA MỀM.
	 * Cần thiết cho logic đồng bộ hóa.
	 */
	@Query("SELECT w FROM CoreWorkSpaceItem w WHERE w.ownerType = :ownerType AND w.ownerValue = :ownerValue AND w.appCode = :appCode")
	List<CoreWorkSpaceItem> findAllByOwnerInAppIncludingDeleted(
			@Param("ownerType") String ownerType,
			@Param("ownerValue") String ownerValue,
			@Param("appCode") String appCode
	                                                           );
	
	/**
	 * Tìm tất cả các item đang hoạt động của một chủ sở hữu trong một ứng dụng, sắp xếp theo thứ tự.
	 */
	List<CoreWorkSpaceItem> findByOwnerTypeAndOwnerValueAndAppCodeOrderBySortOrderAsc(String ownerType, String ownerValue, String appCode);
	
	/**
	 * Kiểm tra xem một item có các item con hay không.
	 * Cần thiết cho việc kiểm tra ràng buộc xóa.
	 */
	boolean existsByParentId(Long parentId);
}
