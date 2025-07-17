package vn.tr.core.dao.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import vn.tr.common.jpa.helper.CriteriaBuilderHelper;
import vn.tr.core.dao.model.CoreUserType;
import vn.tr.core.dao.model.CoreUserType_;
import vn.tr.core.data.criteria.CoreUserTypeSearchCriteria;

public class CoreUserTypeSpecifications {
	
	private CoreUserTypeSpecifications() {
	
	}
	
	public static Specification<CoreUserType> quickSearch(final CoreUserTypeSearchCriteria criteria) {
		return (root, query, cb) -> {
			Predicate textSearchCondition = CriteriaBuilderHelper.createOrUnaccentedLike(cb, root, criteria.getSearch(),
					CoreUserType_.name, CoreUserType_.code);
			Predicate statusCondition = CriteriaBuilderHelper.createEquals(cb, root, CoreUserType_.status, criteria.getStatus());
			Predicate idsCondition = CriteriaBuilderHelper.createIn(cb, root, CoreUserType_.id, criteria.getIds());
			return CriteriaBuilderHelper.and(cb, textSearchCondition, statusCondition, idsCondition);
		};
	}
}
