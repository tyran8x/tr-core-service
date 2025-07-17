package vn.tr.core.dao.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import vn.tr.common.jpa.helper.CriteriaBuilderHelper;
import vn.tr.core.dao.model.CoreRole;
import vn.tr.core.dao.model.CoreRole_;
import vn.tr.core.data.criteria.CoreRoleSearchCriteria;

public class CoreRoleSpecifications {
	
	private CoreRoleSpecifications() {
	
	}
	
	public static Specification<CoreRole> quickSearch(final CoreRoleSearchCriteria criteria) {
		return (root, query, cb) -> {
			Predicate textSearchCondition = CriteriaBuilderHelper.createOrUnaccentedLike(cb, root, criteria.getSearch(),
					CoreRole_.name, CoreRole_.code);
			Predicate statusCondition = CriteriaBuilderHelper.createEquals(cb, root, CoreRole_.status, criteria.getStatus());
			Predicate idsCondition = CriteriaBuilderHelper.createIn(cb, root, CoreRole_.id, criteria.getIds());
			return CriteriaBuilderHelper.and(cb, textSearchCondition, statusCondition, idsCondition);
		};
	}
}
