package vn.tr.core.dao.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import vn.tr.common.jpa.helper.CriteriaBuilderHelper;
import vn.tr.core.dao.model.CoreTag;
import vn.tr.core.dao.model.CoreTag_;
import vn.tr.core.data.criteria.CoreTagSearchCriteria;

public class CoreTagSpecifications {
	
	private CoreTagSpecifications() {
	
	}
	
	public static Specification<CoreTag> quickSearch(final CoreTagSearchCriteria criteria) {
		return (root, query, cb) -> {
			Predicate textSearchCondition = CriteriaBuilderHelper.createOrUnaccentedLike(cb, root, criteria.getSearch(),
					CoreTag_.name, CoreTag_.code);
			Predicate statusCondition = CriteriaBuilderHelper.createEquals(cb, root, CoreTag_.status, criteria.getStatus());
			Predicate idsCondition = CriteriaBuilderHelper.createIn(cb, root, CoreTag_.id, criteria.getIds());
			return CriteriaBuilderHelper.and(cb, textSearchCondition, statusCondition, idsCondition);
		};
	}
}
