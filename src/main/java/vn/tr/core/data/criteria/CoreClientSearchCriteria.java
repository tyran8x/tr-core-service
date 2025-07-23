package vn.tr.core.data.criteria;

import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.common.web.data.criteria.BaseSearchCriteria;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class CoreClientSearchCriteria extends BaseSearchCriteria {
	
	private Set<Long> ids;
	
	private String appCode;
	
	private LifecycleStatus status;
	
}
