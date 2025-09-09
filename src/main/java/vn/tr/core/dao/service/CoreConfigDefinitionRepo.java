package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreConfigDefinition;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho thực thể CoreConfigDefinition.
 *
 * @author tyran8x
 * @version 2.0
 */
@Repository
public interface CoreConfigDefinitionRepo extends JpaRepository<CoreConfigDefinition, Long>, JpaSpecificationExecutor<CoreConfigDefinition> {
	
	boolean existsByIdNotAndKeyIgnoreCaseAndAppCode(long id, String key, @Nullable String appCode);
	
	boolean existsByKeyIgnoreCaseAndAppCode(String key, @Nullable String appCode);
	
	Optional<CoreConfigDefinition> findByKeyAndAppCode(String key, @Nullable String appCode);
	
	/**
	 * Tìm kiếm một ConfigDefinition theo key và appCode, BAO GỒM CẢ BẢN GHI ĐÃ BỊ XÓA MỀM.
	 * Sắp xếp để ưu tiên bản ghi active và được cập nhật gần nhất.
	 * Cần thiết cho logic Upsert.
	 *
	 * @param key     Key của định nghĩa cấu hình.
	 * @param appCode Mã của ứng dụng.
	 *
	 * @return Một danh sách các CoreConfigDefinition tìm thấy, đã được sắp xếp.
	 */
	@Query("SELECT d FROM CoreConfigDefinition d WHERE d.key = :key AND d.appCode = :appCode ORDER BY d.deletedAt ASC NULLS FIRST, d.updatedAt DESC")
	List<CoreConfigDefinition> findByKeyAndAppCodeIncludingDeletedSorted(@Param("key") String key, @Param("appCode") String appCode);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CoreConfigDefinition d SET d.deletedAt = CURRENT_TIMESTAMP WHERE d.id IN :ids")
	void softDeleteByIds(@Param("ids") Collection<Long> ids);
}
