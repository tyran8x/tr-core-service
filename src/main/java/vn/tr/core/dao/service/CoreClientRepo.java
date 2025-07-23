package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreClient;

import java.util.Collection;
import java.util.List;

/**
 * Repository interface cho thực thể CoreClient.
 *
 * @author tyran8x
 * @version 2.0
 */
@Repository
public interface CoreClientRepo extends JpaRepository<CoreClient, Long>, JpaSpecificationExecutor<CoreClient> {
	
	// --- Validation Methods ---
	boolean existsByClientIdAndAppCode(String clientId, String appCode);
	
	boolean existsByIdNotAndClientIdAndAppCode(Long id, String clientId, String appCode);
	
	// --- Query Methods ---
	
	/**
	 * Tìm kiếm Client theo clientId và appCode, BAO GỒM CẢ BẢN GHI ĐÃ BỊ XÓA MỀM.
	 * Sắp xếp để ưu tiên bản ghi active và được cập nhật gần nhất.
	 */
	@Query(
			"SELECT c FROM CoreClient c WHERE c.clientId = :clientId AND c.appCode = :appCode ORDER BY c.deletedAt ASC NULLS FIRST, c.updatedAt DESC"
	)
	List<CoreClient> findByClientIdAndAppCodeIncludingDeletedSorted(@Param("clientId") String clientId, @Param("appCode") String appCode);
	
	// --- Soft Deletion ---
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CoreClient c SET c.deletedAt = CURRENT_TIMESTAMP WHERE c.id IN :ids")
	void softDeleteByIds(@Param("ids") Collection<Long> ids);
}
