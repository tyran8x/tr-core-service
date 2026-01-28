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
public class CoreWorkSpaceItemData extends BaseData {
	
	private Long parentId;
	
	@JsonIgnore
	private String appCode;
	
	@Builder.Default
	private BaseData parent = new BaseData();
	
	private Long ownerId;
	
	private String ownerType;
	
	private String icon;
	
	private String itemType;
	
	private String itemConfig;
	
	private String description;
	
	private Integer sortOrder;
	
}
