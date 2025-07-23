package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreUser;

import java.util.Collection;
import java.util.Optional;

/**
 * Repository interface cho thực thể CoreUser.
 *
 * @author tyran8x
 * @version 2.0
 */
@Repository
public interface CoreUserRepo extends JpaRepository<CoreUser, Long>, JpaSpecificationExecutor<CoreUser> {
	
	/**
	 * Tìm một người dùng theo username (không phân biệt hoa thường).
	 * Chỉ trả về các bản ghi đang hoạt động.
	 */
	Optional<CoreUser> findFirstByUsernameIgnoreCase(String username);
	
	/**
	 * Tìm một người dùng theo username (không phân biệt hoa thường),
	 * BAO GỒM CẢ CÁC BẢN GHI ĐÃ BỊ XÓA MỀM.
	 * Cần thiết cho logic Upsert.
	 */
	@Query("SELECT u FROM CoreUser u WHERE lower(u.username) = lower(:username)")
	Optional<CoreUser> findByUsernameIgnoreCaseIncludingDeleted(@Param("username") String username);
	
	/**
	 * Kiểm tra sự tồn tại của một người dùng theo username (không phân biệt hoa thường).
	 */
	boolean existsByUsernameIgnoreCase(String username);
	
	/**
	 * Thực hiện xóa mềm cho một tập hợp các ID người dùng.
	 *
	 * @param ids Collection các ID của người dùng cần xóa.
	 */
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CoreUser u SET u.deletedAt = CURRENT_TIMESTAMP WHERE u.id IN :ids")
	void softDeleteByIds(@Param("ids") Collection<Long> ids);
	
	Optional<CoreUser> findFirstByEmailIgnoreCase(String email);
	
	boolean existsByEmailIgnoreCase(String email);
}
