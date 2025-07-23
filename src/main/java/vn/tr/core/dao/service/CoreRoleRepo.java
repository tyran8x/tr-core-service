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
import java.util.Set;

@Repository
public interface CoreRoleRepo extends JpaRepository<CoreRole, Long>, JpaSpecificationExecutor<CoreRole> {
	
	boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, @Nullable String appCode);
	
	boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, @Nullable String appCode);
	
	boolean existsByCodeIgnoreCaseAndAppCode(String code, @Nullable String appCode);
	
	boolean existsByNameIgnoreCaseAndAppCode(String name, @Nullable String appCode);
	
	boolean existsByIdAndAppCode(long id, @Nullable String appCode);
	
	/**
	 * Tìm kiếm Role theo code và appCode, BAO GỒM CẢ CÁC BẢN GHI ĐÃ BỊ XÓA MỀM. Kết quả được sắp xếp để ưu tiên bản ghi đang hoạt động và được cập
	 * nhật gần nhất.
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
	
	/**
	 * BỔ SUNG: Tìm tất cả các mã vai trò (role codes) tồn tại trong một ứng dụng cụ thể, từ một danh sách các mã vai trò cho trước. Phương thức này
	 * rất hiệu quả để lọc và xác thực hàng loạt.
	 *
	 * @param appCode   Mã ứng dụng.
	 * @param roleCodes Collection các mã vai trò cần kiểm tra.
	 *
	 * @return Một Set chứa các mã vai trò hợp lệ trong ứng dụng đó.
	 */
	@Query("SELECT r.code FROM CoreRole r WHERE r.appCode = :appCode AND r.code IN :roleCodes")
	Set<String> findExistingRoleCodesInApp(@Param("appCode") String appCode, @Param("roleCodes") Collection<String> roleCodes);
	
	/**
	 * BỔ SUNG: Tìm tất cả các thực thể CoreRole dựa trên appCode và một danh sách các code. Phương thức này là "nguyên liệu" bắt buộc cho
	 * AssociationSyncHelper khi đồng bộ User-Role.
	 *
	 * @param appCode Mã ứng dụng.
	 * @param codes   Collection các mã vai trò.
	 *
	 * @return Danh sách các thực thể CoreRole tìm thấy.
	 */
	List<CoreRole> findAllByAppCodeAndCodeIn(String appCode, Collection<String> codes);
	
}
