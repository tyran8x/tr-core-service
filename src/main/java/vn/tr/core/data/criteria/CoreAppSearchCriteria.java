package vn.tr.core.data.criteria;

import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.tr.common.web.data.criteria.BaseSearchCriteria;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class CoreAppSearchCriteria extends BaseSearchCriteria {
	
	private Set<Long> ids;
	
	private String name;
	
	private String code;
	
	private String status;
	
}
