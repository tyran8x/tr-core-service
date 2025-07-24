package vn.tr.core.data.dto;

import lombok.Data;
import vn.tr.common.core.enums.LifecycleStatus;

@Data
public class CoreConfigDefinitionData {
	
	private Long id;
	
	private String key;
	
	private String name;
	
	private String description;
	
	private String dataType = "STRING";
	
	private String validationRules;
	
	private String defaultValue;
	
	private String appCode;
	
	private Boolean isPublic;
	
	private Boolean isEncrypted;
	
	private LifecycleStatus status = LifecycleStatus.ACTIVE;
	
	private Integer sortOrder = 0;
	
}
