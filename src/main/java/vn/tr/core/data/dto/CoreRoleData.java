package vn.tr.core.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vn.tr.common.web.data.dto.BaseData;

import java.util.HashSet;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CoreRoleData extends BaseData {
	
	@JsonIgnore
	private String appCode;
	
	private String description;
	
	private Integer sortOrder;
	
	@Builder.Default
	private Set<String> permissionCodes = new HashSet<>();
	
}
