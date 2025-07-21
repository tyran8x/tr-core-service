package vn.tr.core.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class CoreContactData {
	
	private Long id;
	
	private String contactType;
	
	private String label;
	
	private String value;
	
	private Boolean isPrimary;
	
	@JsonIgnore
	private String appCode;
	
}
