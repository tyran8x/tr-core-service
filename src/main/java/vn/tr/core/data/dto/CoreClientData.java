package vn.tr.core.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vn.tr.common.core.enums.LifecycleStatus;

/**
 * DTO cho thực thể CoreClient.
 *
 * @author tyran8x
 * @version 2.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class CoreClientData {
	
	private Long id;
	
	@Size(max = 64, message = "Client ID không được vượt quá 64 ký tự")
	private String clientId;
	
	@Size(max = 32, message = "Client Key không được vượt quá 32 ký tự")
	private String clientKey;
	
	// Client Secret không nên được trả về trong response, chỉ nhận khi tạo/cập nhật
	// Sử dụng @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) nếu cần
	@Size(max = 250, message = "Client Secret không được vượt quá 250 ký tự")
	private String clientSecret;
	
	@Size(max = 250, message = "Grant Type không được vượt quá 250 ký tự")
	private String grantType;
	
	@Size(max = 32, message = "Device Type không được vượt quá 32 ký tự")
	private String deviceType;
	
	private Integer activeTimeout;
	
	private Integer timeout;
	
	private Integer sortOrder;
	
	private Boolean isSecretRequired;
	
	private LifecycleStatus status;
	// appCode được quản lý bởi backend, không nên nhận từ client
	@JsonIgnore
	private String appCode;
	
}
