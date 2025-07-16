package vn.tr.core.dao.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import vn.tr.common.jpa.helper.CriteriaBuilderHelper;
import vn.tr.core.dao.model.CoreApp;
import vn.tr.core.dao.model.CoreApp_;
import vn.tr.core.data.criteria.CoreAppSearchCriteria;

public class CoreAppSpecifications {
	
	private CoreAppSpecifications() {
	
	}
	
	public static Specification<CoreApp> quickSearch(final CoreAppSearchCriteria criteria) {
		return (root, query, cb) -> {
			Predicate textSearchCondition = CriteriaBuilderHelper.createOrUnaccentedLike(cb, root, criteria.getSearch(),
					CoreApp_.name, CoreApp_.code);
			Predicate statusCondition = CriteriaBuilderHelper.createEquals(cb, root, CoreApp_.status, criteria.getStatus());
			Predicate idsCondition = CriteriaBuilderHelper.createIn(cb, root, CoreApp_.id, criteria.getIds());
			return CriteriaBuilderHelper.and(cb, textSearchCondition, statusCondition, idsCondition);
		};
	}
}
