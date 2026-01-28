package vn.tr.core.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vn.tr.common.web.data.dto.BaseData;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CoreUserTypeData extends BaseData {
	
	@JsonIgnore
	private String appCode;
	
	private String description;
	
	private String icon;
	
	private Integer sortOrder;
	
}
