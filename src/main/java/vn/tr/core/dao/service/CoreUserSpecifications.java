package vn.tr.core.dao.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import vn.tr.common.core.utils.StringUtils;
import vn.tr.common.jpa.helper.CriteriaBuilderHelper;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.dao.model.CoreUserApp;
import vn.tr.core.dao.model.CoreUserApp_;
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
			Predicate businessLogicCondition = createUserAppExistsPredicate(query, cb, root, criteria);
			return CriteriaBuilderHelper.and(cb, textSearchCondition, statusCondition, idsCondition, businessLogicCondition);
		};
	}
	
	private static Predicate createUserAppExistsPredicate(CriteriaQuery<?> query, CriteriaBuilder cb, Root<CoreUser> root,
			CoreUserSearchCriteria criteria) {
		if (StringUtils.isNotEmpty(criteria.getAppCode())) {
			return CriteriaBuilderHelper.createExists(query, cb, root, CoreUserApp.class, (subRoot, parentRoot, subCb) -> {
				Predicate joinCond = subCb.equal(subRoot.get(CoreUserApp_.username), parentRoot.get(CoreUser_.username));
				return CriteriaBuilderHelper.and(subCb, joinCond);
			});
		}
		return null;
	}
}
