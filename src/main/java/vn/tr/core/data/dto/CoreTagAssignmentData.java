package vn.tr.core.data.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class CoreTagAssignmentData {
	
	private Long id;
	
	private String tagCode;
	
	private String taggableType;
	
	private String taggableValue;
	
	private Integer sortOrder;
	
}
