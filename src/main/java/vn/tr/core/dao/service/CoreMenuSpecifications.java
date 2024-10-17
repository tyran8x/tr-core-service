package vn.tr.core.dao.service;

import cn.hutool.core.text.CharSequenceUtil;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.core.dao.model.CoreMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CoreMenuSpecifications {

	private CoreMenuSpecifications() {

	}

	public static Specification<CoreMenu> quickSearch(final String search, final Boolean trangThai, final String appCode) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.<String>get("daXoa"), false));

			if (CharSequenceUtil.isNotBlank(search)) {
				Predicate pTen = cb.like(cb.lower(cb.function(CoreUtils.UNACCENT_VN, String.class, root.<String>get("ten"))),
						"%" + CoreUtils.removeAccent(search.toLowerCase().trim()) + "%");
				Predicate pMa = cb.like(cb.lower(root.get("ma")), "%" + search.toLowerCase() + "%");
				predicates.add(cb.or(pTen, pMa));
			}
			if (Objects.nonNull(trangThai)) {
				predicates.add(cb.equal(root.<String>get("trangThai"), trangThai));
			}
			if (CharSequenceUtil.isNotBlank(appCode)) {
				predicates.add(cb.equal(cb.lower(root.get("appCode")), appCode.toLowerCase()));
			}
			return cb.and(predicates.toArray(new Predicate[]{}));

		};
	}
}
