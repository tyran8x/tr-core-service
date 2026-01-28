package vn.tr.core.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vn.tr.common.web.data.dto.BaseData;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CoreGroupData extends BaseData {
	
	private Long parentId;
	
	@JsonIgnore
	private String appCode;
	
	@Builder.Default
	private BaseData parent = new BaseData();
	
	private String description;
	
	private Integer sortOrder;
	
}
