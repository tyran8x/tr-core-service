package vn.tr.core.dao.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import vn.tr.common.jpa.helper.CriteriaBuilderHelper;
import vn.tr.core.dao.model.CoreWorkSpaceItem;
import vn.tr.core.dao.model.CoreWorkSpaceItem_;
import vn.tr.core.data.criteria.CoreWorkSpaceItemSearchCriteria;

public class CoreWorkSpaceItemSpecifications {
	
	private CoreWorkSpaceItemSpecifications() {
	
	}
	
	public static Specification<CoreWorkSpaceItem> quickSearch(final CoreWorkSpaceItemSearchCriteria criteria) {
		return (root, query, cb) -> {
			Predicate textSearchCondition = CriteriaBuilderHelper.createOrUnaccentedLike(cb, root, criteria.getSearch(),
					CoreWorkSpaceItem_.name, CoreWorkSpaceItem_.code);
			Predicate statusCondition = CriteriaBuilderHelper.createEquals(cb, root, CoreWorkSpaceItem_.status, criteria.getStatus());
			Predicate idsCondition = CriteriaBuilderHelper.createIn(cb, root, CoreWorkSpaceItem_.id, criteria.getIds());
			return CriteriaBuilderHelper.and(cb, textSearchCondition, statusCondition, idsCondition);
		};
	}
}
