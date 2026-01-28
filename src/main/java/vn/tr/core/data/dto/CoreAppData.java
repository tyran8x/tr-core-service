package vn.tr.core.data.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vn.tr.common.web.data.dto.BaseData;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CoreAppData extends BaseData {
	
	private String description;
	
	private Integer sortOrder;
	
}
