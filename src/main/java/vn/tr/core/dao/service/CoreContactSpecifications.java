package vn.tr.core.dao.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import vn.tr.common.jpa.helper.CriteriaBuilderHelper;
import vn.tr.core.dao.model.CoreContact;
import vn.tr.core.dao.model.CoreContact_;
import vn.tr.core.data.criteria.CoreContactSearchCriteria;

public class CoreContactSpecifications {
	
	private CoreContactSpecifications() {
	
	}
	
	public static Specification<CoreContact> quickSearch(final CoreContactSearchCriteria criteria) {
		return (root, query, cb) -> {
			Predicate textSearchCondition = CriteriaBuilderHelper.createOrUnaccentedLike(cb, root, criteria.getSearch(),
					CoreContact_.value, CoreContact_.label);
			Predicate statusCondition = CriteriaBuilderHelper.createEquals(cb, root, CoreContact_.status, criteria.getStatus());
			Predicate idsCondition = CriteriaBuilderHelper.createIn(cb, root, CoreContact_.id, criteria.getIds());
			return CriteriaBuilderHelper.and(cb, textSearchCondition, statusCondition, idsCondition);
		};
	}
}
