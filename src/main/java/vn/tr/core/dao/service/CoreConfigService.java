package vn.tr.core.dao.service;

import vn.tr.common.core.enums.ConfigScope;

import java.util.Map;
import java.util.Optional;

/**
 * Dịch vụ trung tâm để đọc và ghi cấu hình động một cách an toàn và hiệu quả.
 * Đây là API cấp cao, đóng gói toàn bộ logic phức tạp về scope, cache, và mã hóa.
 *
 * @author tyran8x
 * @version 2.0
 */
public interface CoreConfigService {
	
	/**
	 * Lấy giá trị của một cấu hình dưới dạng String.
	 * Tự động tìm kiếm theo thứ tự ưu tiên của scope.
	 *
	 * @param key        Key của cấu hình (ví dụ: "notification.email.provider").
	 * @param scopeChain Một Map chứa các scope và giá trị của chúng (ví dụ: {USER: "userA", APP: "appB"}).
	 *
	 * @return Optional chứa giá trị String.
	 */
	Optional<String> getString(String key, Map<ConfigScope, String> scopeChain);
	
	/**
	 * Lấy giá trị của một cấu hình và chuyển đổi sang kiểu Integer.
	 */
	Optional<Integer> getInteger(String key, Map<ConfigScope, String> scopeChain);
	
	/**
	 * Lấy giá trị của một cấu hình và chuyển đổi sang kiểu Boolean.
	 */
	Optional<Boolean> getBoolean(String key, Map<ConfigScope, String> scopeChain);
	
	/**
	 * Lấy giá trị của một cấu hình và chuyển đổi sang một đối tượng tùy chỉnh từ JSON.
	 *
	 * @param targetType Class của đối tượng DTO mong muốn.
	 */
	<T> Optional<T> getObject(String key, Map<ConfigScope, String> scopeChain, Class<T> targetType);
	
	/**
	 * Ghi/cập nhật một giá trị cấu hình cho một scope cụ thể.
	 *
	 * @param appCode    Ngữ cảnh ứng dụng của định nghĩa cấu hình.
	 * @param key        Key của cấu hình.
	 * @param scope      Scope cần ghi (ví dụ: USER).
	 * @param scopeValue Giá trị của scope (ví dụ: "userA").
	 * @param value      Giá trị mới cần lưu (sẽ được chuyển thành String hoặc JSON).
	 */
	void setValue(String appCode, String key, ConfigScope scope, String scopeValue, Object value);
}
