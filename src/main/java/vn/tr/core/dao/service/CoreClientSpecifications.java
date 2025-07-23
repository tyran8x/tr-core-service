package vn.tr.core.dao.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import vn.tr.common.jpa.helper.CriteriaBuilderHelper;
import vn.tr.core.dao.model.CoreClient;
import vn.tr.core.dao.model.CoreClient_;
import vn.tr.core.data.criteria.CoreClientSearchCriteria;

public class CoreClientSpecifications {
	
	private CoreClientSpecifications() {
	
	}
	
	public static Specification<CoreClient> quickSearch(final CoreClientSearchCriteria criteria) {
		return (root, query, cb) -> {
			Predicate textSearchCondition = CriteriaBuilderHelper.createOrUnaccentedLike(cb, root, criteria.getSearch(),
					CoreClient_.clientId, CoreClient_.clientKey);
			Predicate statusCondition = CriteriaBuilderHelper.createEquals(cb, root, CoreClient_.status, criteria.getStatus());
			Predicate idsCondition = CriteriaBuilderHelper.createIn(cb, root, CoreClient_.id, criteria.getIds());
			return CriteriaBuilderHelper.and(cb, textSearchCondition, statusCondition, idsCondition);
		};
	}
}
