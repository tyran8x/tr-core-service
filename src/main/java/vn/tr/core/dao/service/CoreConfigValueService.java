package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.tr.core.dao.model.CoreConfigValue;

import java.util.Optional;

/**
 * Interface cho Service Layer của CoreConfigValue.
 * Cung cấp các nghiệp vụ cơ bản và tái sử dụng liên quan đến giá trị cấu hình.
 *
 * @author tyran8x
 * @version 2.0
 */
public interface CoreConfigValueService {
	
	/**
	 * Tìm hoặc tạo/cập nhật một giá trị cấu hình cho một scope cụ thể.
	 * Đây là nghiệp vụ upsert chính.
	 *
	 * @param definitionId ID của CoreConfigDefinition.
	 * @param scopeType    Loại scope.
	 * @param scopeValue   Giá trị của scope.
	 * @param newValue     Giá trị mới cần lưu.
	 *
	 * @return Thực thể CoreConfigValue đã được lưu.
	 */
	CoreConfigValue upsertValue(Long definitionId, String scopeType, String scopeValue, String newValue);
	
	/**
	 * Tìm giá trị cấu hình đang hoạt động cho một definition và scope cụ thể.
	 */
	Optional<CoreConfigValue> findActiveValue(Long definitionId, String scopeType, String scopeValue);
	
	/**
	 * Kiểm tra xem một định nghĩa cấu hình có đang được sử dụng hay không.
	 *
	 * @param definitionId ID của CoreConfigDefinition.
	 *
	 * @return true nếu đang được sử dụng.
	 */
	boolean isDefinitionInUse(Long definitionId);
	
	/**
	 * Trả về JpaRepository để các helper có thể sử dụng.
	 */
	JpaRepository<CoreConfigValue, Long> getRepository();
}
