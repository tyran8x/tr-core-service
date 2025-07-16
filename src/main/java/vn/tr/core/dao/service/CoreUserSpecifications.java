package vn.tr.core.dao.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import vn.tr.common.jpa.helper.CriteriaBuilderHelper;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.dao.model.CoreUser_;
import vn.tr.core.data.criteria.CoreUserSearchCriteria;

public class CoreUserSpecifications {
	
	private CoreUserSpecifications() {
	
	}
	
	public static Specification<CoreUser> quickSearch(final CoreUserSearchCriteria criteria) {
		return (root, query, cb) -> {
			Predicate textSearchCondition = CriteriaBuilderHelper.createOrUnaccentedLike(cb, root, criteria.getSearch(),
					CoreUser_.username, CoreUser_.email);
			Predicate statusCondition = CriteriaBuilderHelper.createEquals(cb, root, CoreUser_.status, criteria.getStatus());
			Predicate idsCondition = CriteriaBuilderHelper.createIn(cb, root, CoreUser_.id, criteria.getIds());
			return CriteriaBuilderHelper.and(cb, textSearchCondition, statusCondition, idsCondition);
		};
	}
}
