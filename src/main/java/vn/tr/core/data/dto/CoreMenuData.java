package vn.tr.core.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vn.tr.common.web.data.dto.BaseData;

import java.util.List;
import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoreMenuData extends BaseData {
	
	private Long parentId;
	
	@NotEmpty(message = "Mã ứng dụng không được để trống")
	private String appCode;
	
	private String permissionCode;
	private String path;
	private String component;
	private String icon;
	
	@Builder.Default
	private Integer sortOrder = 0;
	
	private Boolean isHidden;
	
	// Cho phép admin cấu hình các thuộc tính meta nâng cao nếu cần
	private Map<String, Object> extraMeta;
	
	// Dùng để trả về cấu trúc cây
	private List<CoreMenuData> children;
}
