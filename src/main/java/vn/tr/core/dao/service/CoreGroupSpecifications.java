package vn.tr.core.dao.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import vn.tr.common.jpa.helper.CriteriaBuilderHelper;
import vn.tr.core.dao.model.CoreGroup;
import vn.tr.core.dao.model.CoreGroup_;
import vn.tr.core.data.criteria.CoreGroupSearchCriteria;

public class CoreGroupSpecifications {
	
	private CoreGroupSpecifications() {
	
	}
	
	public static Specification<CoreGroup> quickSearch(final CoreGroupSearchCriteria criteria) {
		return (root, query, cb) -> {
			Predicate textSearchCondition = CriteriaBuilderHelper.createOrUnaccentedLike(cb, root, criteria.getSearch(),
					CoreGroup_.name, CoreGroup_.code);
			Predicate statusCondition = CriteriaBuilderHelper.createEquals(cb, root, CoreGroup_.status, criteria.getStatus());
			return CriteriaBuilderHelper.and(cb, textSearchCondition, statusCondition);
		};
	}
}
