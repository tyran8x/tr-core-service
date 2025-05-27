package vn.tr.core.dao.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.core.dao.model.CoreUser;

import java.util.ArrayList;
import java.util.List;

public class CoreUserSpecifications {
	
	private CoreUserSpecifications() {
	
	}
	
	public static Specification<CoreUser> quickSearch(final String search, final String email, final String name, final List<String> roles,
			final String appCode) {
		
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.<String>get("daXoa"), false));
			if (CharSequenceUtil.isNotBlank(search)) {
				Predicate pEmail = cb.like(cb.lower(root.get("email")), "%" + CoreUtils.removeAccent(search.toLowerCase().trim()) + "%");
				Predicate pNickName = cb.like(cb.lower(root.get("nickName")), "%" + CoreUtils.removeAccent(search.toLowerCase().trim()) + "%");
				Predicate pUserName = cb.like(cb.lower(root.get("userName")), "%" + CoreUtils.removeAccent(search.toLowerCase().trim()) + "%");
				predicates.add(cb.or(pEmail, pNickName, pUserName));
			}
			if (CharSequenceUtil.isNotBlank(email)) {
				predicates.add(cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase().trim() + "%"));
			}
			if (CharSequenceUtil.isNotBlank(name)) {
				predicates.add(cb.like(cb.lower(cb.function(CoreUtils.UNACCENT_VN, String.class, root.get("name"))),
						"%" + CoreUtils.removeAccent(name.toLowerCase().trim()) + "%"));
			}
			if (CollUtil.isNotEmpty(roles)) {
				Expression<String> expression = root.join("coreRoles").get("role");
				Predicate inList = expression.in(roles);
				predicates.add(inList);
			}
			if (CharSequenceUtil.isNotBlank(appCode)) {
				predicates.add(cb.equal(root.<String>get("appCode"), appCode));
			}
			return cb.and(predicates.toArray(new Predicate[]{}));
		};
	}
}
