package vn.tr.core.dao.service;

import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.core.dao.model.CoreModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CoreModuleSpecifications {

	private CoreModuleSpecifications() {

	}

	public static Specification<CoreModule> quickSearch(final String search, final Boolean trangThai) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.<String>get("daXoa"), false));

			if (StringUtils.isNotBlank(search)) {
				Predicate pTen = cb.like(cb.lower(cb.function(CoreUtils.UNACCENT_VN, String.class, root.<String>get("ten"))),
						"%" + CoreUtils.removeAccent(search.toLowerCase().trim()) + "%");
				Predicate pMa = cb.like(cb.lower(root.get("ma")), "%" + search.toLowerCase() + "%");
				predicates.add(cb.or(pTen, pMa));
			}
			if (Objects.nonNull(trangThai)) {
				predicates.add(cb.equal(root.<String>get("trangThai"), trangThai));
			}
			return cb.and(predicates.toArray(new Predicate[]{}));

		};
	}
}
