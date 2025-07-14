package vn.tr.core.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vn.tr.common.core.domain.data.BaseCatalogData;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CoreGroupData extends BaseCatalogData {
	
	private Long id;
	
	private Long parentId;
	
	private Long appId;
	
	private BaseCatalogData parent = new BaseCatalogData();
	
	private String description;
	
	private Integer sortOrder;
	
}
