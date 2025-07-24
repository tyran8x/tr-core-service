package vn.tr.core.dao.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import vn.tr.common.jpa.helper.CriteriaBuilderHelper;
import vn.tr.core.dao.model.CoreConfigDefinition;
import vn.tr.core.dao.model.CoreConfigDefinition_;
import vn.tr.core.data.criteria.CoreConfigDefinitionSearchCriteria;

public class CoreConfigDefinitionSpecifications {
	
	private CoreConfigDefinitionSpecifications() {
	
	}
	
	public static Specification<CoreConfigDefinition> quickSearch(final CoreConfigDefinitionSearchCriteria criteria) {
		return (root, query, cb) -> {
			Predicate textSearchCondition = CriteriaBuilderHelper.createOrUnaccentedLike(cb, root, criteria.getSearch(),
					CoreConfigDefinition_.key, CoreConfigDefinition_.defaultValue);
			Predicate statusCondition = CriteriaBuilderHelper.createEquals(cb, root, CoreConfigDefinition_.status, criteria.getStatus());
			Predicate idsCondition = CriteriaBuilderHelper.createIn(cb, root, CoreConfigDefinition_.id, criteria.getIds());
			return CriteriaBuilderHelper.and(cb, textSearchCondition, statusCondition, idsCondition);
		};
	}
}
