package vn.tr.core.data.criteria;

import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.tr.common.web.criteria.SearchCriteria;

@Data
@EqualsAndHashCode(callSuper = true)
public class CoreGroupSearchCriteria extends SearchCriteria {
	
	private Long parentId;
	
	private String name;
	
	private String code;
	
	private String status;
	
}
