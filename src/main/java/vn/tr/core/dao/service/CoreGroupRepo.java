package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreGroup;

import java.util.Collection;
import java.util.List;

/**
 * Repository interface cho thực thể CoreGroup.
 * Cung cấp các phương thức truy vấn cơ sở dữ liệu, đã được tối ưu cho các nghiệp vụ cụ thể.
 *
 * @author tyran8x
 * @version 2.3 (Optimized Queries)
 */
@Repository
public interface CoreGroupRepo extends JpaRepository<CoreGroup, Long>, JpaSpecificationExecutor<CoreGroup> {
	
	// --- Các phương thức kiểm tra sự tồn tại (dùng cho validation) ---
	boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, @Nullable String appCode);
	
	boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, @Nullable String appCode);
	
	boolean existsByCodeIgnoreCaseAndAppCode(String code, @Nullable String appCode);
	
	boolean existsByNameIgnoreCaseAndAppCode(String name, @Nullable String appCode);
	
	boolean existsByIdAndAppCode(long id, @Nullable String appCode);
	
	// --- Các phương thức tìm kiếm ---
	
	List<CoreGroup> findAllByAppCodeAndCodeIn(String appCode, Collection<String> codes);
	
	/**
	 * Tìm kiếm Group theo code và appCode, BAO GỒM CẢ CÁC BẢN GHI ĐÃ BỊ XÓA MỀM.
	 * Kết quả được sắp xếp để ưu tiên bản ghi đang hoạt động (deletedAt IS NULL) và được cập nhật gần nhất.
	 * Điều này rất quan trọng để tầng Service có thể xử lý an toàn trường hợp dữ liệu trùng lặp.
	 *
	 * @param code    Mã của nhóm.
	 * @param appCode Mã của ứng dụng.
	 *
	 * @return Một danh sách các CoreGroup tìm thấy, đã được sắp xếp theo quy tắc ưu tiên.
	 */
	@Query("SELECT g FROM CoreGroup g WHERE g.code = :code AND g.appCode = :appCode ORDER BY g.deletedAt ASC NULLS FIRST, g.updatedAt DESC")
	List<CoreGroup> findAllByCodeAndAppCodeIncludingDeletedSorted(@Param("code") String code, @Param("appCode") String appCode);
	
	@Override
	@EntityGraph(attributePaths = {"parent"})
	Page<CoreGroup> findAll(@Nullable Specification<CoreGroup> spec, Pageable pageable);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CoreGroup g SET g.deletedAt = CURRENT_TIMESTAMP WHERE g.id IN :ids")
	void softDeleteByIds(@Param("ids") Collection<Long> ids);
}
