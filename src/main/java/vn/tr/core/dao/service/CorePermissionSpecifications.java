package vn.tr.core.dao.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import vn.tr.common.jpa.helper.CriteriaBuilderHelper;
import vn.tr.core.dao.model.CorePermission;
import vn.tr.core.dao.model.CorePermission_;
import vn.tr.core.data.criteria.CorePermissionSearchCriteria;

public class CorePermissionSpecifications {
	
	private CorePermissionSpecifications() {
	
	}
	
	public static Specification<CorePermission> quickSearch(final CorePermissionSearchCriteria criteria) {
		return (root, query, cb) -> {
			Predicate textSearchCondition = CriteriaBuilderHelper.createOrUnaccentedLike(cb, root, criteria.getSearch(),
					CorePermission_.name, CorePermission_.code);
			Predicate statusCondition = CriteriaBuilderHelper.createEquals(cb, root, CorePermission_.status, criteria.getStatus());
			Predicate idsCondition = CriteriaBuilderHelper.createIn(cb, root, CorePermission_.id, criteria.getIds());
			return CriteriaBuilderHelper.and(cb, textSearchCondition, statusCondition, idsCondition);
		};
	}
}
