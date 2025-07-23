package vn.tr.core.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vn.tr.common.json.handler.XssPlainTextJsonDeserializer;
import vn.tr.common.web.data.dto.BaseData;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CorePermissionData extends BaseData {
	
	@JsonIgnore
	private String appCode;
	
	@JsonDeserialize(using = XssPlainTextJsonDeserializer.class)
	private String description;
	
	private Integer sortOrder;
	
	private Long moduleId;
	
}
