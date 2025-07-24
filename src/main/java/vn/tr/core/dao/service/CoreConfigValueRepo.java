package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreConfigValue;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho thực thể CoreConfigValue.
 *
 * @author tyran8x
 * @version 2.0
 */
@Repository
public interface CoreConfigValueRepo extends JpaRepository<CoreConfigValue, Long>, JpaSpecificationExecutor<CoreConfigValue> {
	
	/**
	 * Tìm một giá trị cấu hình cho một definition và một scope cụ thể.
	 * BAO GỒM CẢ ĐÃ BỊ XÓA MỀM, để có thể upsert/kích hoạt lại.
	 *
	 * @param definitionId ID của CoreConfigDefinition.
	 * @param scopeType    Loại scope (ví dụ: "APP", "USER").
	 * @param scopeValue   Giá trị của scope (ví dụ: "hrm-app", "user-a").
	 *
	 * @return Một danh sách các giá trị tìm thấy (để xử lý trường hợp trùng lặp).
	 */
	@Query(
			"SELECT cv FROM CoreConfigValue cv WHERE cv.definitionId = :defId AND cv.scopeType = :scopeType AND cv.scopeValue = :scopeValue " +
					"ORDER BY cv.deletedAt ASC NULLS FIRST, cv.updatedAt DESC"
	)
	List<CoreConfigValue> findByDefinitionAndScopeIncludingDeletedSorted(
			@Param("defId") Long definitionId,
			@Param("scopeType") String scopeType,
			@Param("scopeValue") String scopeValue
	                                                                    );
	
	/**
	 * Tìm giá trị cấu hình đang hoạt động cho một definition và scope cụ thể.
	 */
	Optional<CoreConfigValue> findByDefinitionIdAndScopeTypeAndScopeValue(Long definitionId, String scopeType, String scopeValue);
	
	/**
	 * Kiểm tra xem một định nghĩa cấu hình có đang được sử dụng (có giá trị được gán) hay không.
	 *
	 * @param definitionId ID của CoreConfigDefinition.
	 *
	 * @return true nếu có ít nhất một giá trị được gán, ngược lại false.
	 */
	boolean existsByDefinitionId(Long definitionId);
}
