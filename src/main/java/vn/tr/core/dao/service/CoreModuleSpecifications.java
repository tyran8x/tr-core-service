package vn.tr.core.dao.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import vn.tr.common.jpa.helper.CriteriaBuilderHelper;
import vn.tr.core.dao.model.CoreModule;
import vn.tr.core.dao.model.CoreModule_;
import vn.tr.core.data.criteria.CoreModuleSearchCriteria;

public class CoreModuleSpecifications {
	
	private CoreModuleSpecifications() {
	
	}
	
	public static Specification<CoreModule> quickSearch(final CoreModuleSearchCriteria criteria) {
		return (root, query, cb) -> {
			Predicate textSearchCondition = CriteriaBuilderHelper.createOrUnaccentedLike(cb, root, criteria.getSearch(),
					CoreModule_.name, CoreModule_.code);
			Predicate statusCondition = CriteriaBuilderHelper.createEquals(cb, root, CoreModule_.status, criteria.getStatus());
			Predicate idsCondition = CriteriaBuilderHelper.createIn(cb, root, CoreModule_.id, criteria.getIds());
			return CriteriaBuilderHelper.and(cb, textSearchCondition, statusCondition, idsCondition);
		};
	}
}
