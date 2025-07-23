package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreRole;

import java.util.Collection;
import java.util.List;

@Repository
public interface CoreRoleRepo extends JpaRepository<CoreRole, Long>, JpaSpecificationExecutor<CoreRole> {
	
	boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, @Nullable String appCode);
	
	boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, @Nullable String appCode);
	
	boolean existsByCodeIgnoreCaseAndAppCode(String code, @Nullable String appCode);
	
	boolean existsByNameIgnoreCaseAndAppCode(String name, @Nullable String appCode);
	
	boolean existsByIdAndAppCode(long id, @Nullable String appCode);
	
	/**
	 * Tìm kiếm Role theo code và appCode, BAO GỒM CẢ CÁC BẢN GHI ĐÃ BỊ XÓA MỀM.
	 * Kết quả được sắp xếp để ưu tiên bản ghi đang hoạt động và được cập nhật gần nhất.
	 *
	 * @param code    Mã của module.
	 * @param appCode Mã của ứng dụng.
	 *
	 * @return Một danh sách các CoreRole tìm thấy, đã được sắp xếp.
	 */
	@Query("SELECT m FROM CoreRole m WHERE m.code = :code AND m.appCode = :appCode ORDER BY m.deletedAt ASC NULLS FIRST, m.updatedAt DESC")
	List<CoreRole> findAllByCodeAndAppCodeIncludingDeletedSorted(@Param("code") String code, @Param("appCode") String appCode);
	
	// --- Soft Deletion ---
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CoreRole m SET m.deletedAt = CURRENT_TIMESTAMP WHERE m.id IN :ids")
	void softDeleteByIds(@Param("ids") Collection<Long> ids);
	
}
