package vn.tr.core.dao.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import vn.tr.common.jpa.helper.CriteriaBuilderHelper;
import vn.tr.core.dao.model.CoreMenu;
import vn.tr.core.dao.model.CoreMenu_;
import vn.tr.core.data.criteria.CoreMenuSearchCriteria;

public class CoreMenuSpecifications {
	
	private CoreMenuSpecifications() {
	
	}
	
	public static Specification<CoreMenu> quickSearch(final CoreMenuSearchCriteria criteria) {
		return (root, query, cb) -> {
			Predicate textSearchCondition = CriteriaBuilderHelper.createOrUnaccentedLike(cb, root, criteria.getSearch(),
					CoreMenu_.name, CoreMenu_.code);
			Predicate statusCondition = CriteriaBuilderHelper.createEquals(cb, root, CoreMenu_.status, criteria.getStatus());
			Predicate idsCondition = CriteriaBuilderHelper.createIn(cb, root, CoreMenu_.id, criteria.getIds());
			return CriteriaBuilderHelper.and(cb, textSearchCondition, statusCondition, idsCondition);
		};
	}
}
