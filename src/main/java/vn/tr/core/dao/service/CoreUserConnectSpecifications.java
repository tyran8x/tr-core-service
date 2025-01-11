package vn.tr.core.dao.service;

import cn.hutool.core.collection.CollUtil;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import vn.tr.core.dao.model.CoreUserConnect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CoreUserConnectSpecifications {
	
	private CoreUserConnectSpecifications() {
	
	}
	
	public static Specification<CoreUserConnect> quickSearch(final String search, final Boolean trangThai, final List<String> maUngDungs,
			final String appCode) {
		return (root, query, cb) -> {
			
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.<String>get("daXoa"), false));
			if (search != null && !search.isEmpty()) {
				Predicate ten = cb.like(cb.lower(root.get("ten")), "%" + search.toLowerCase() + "%");
				Predicate ma = cb.like(cb.lower(root.get("maUngDung")), "%" + search.toLowerCase() + "%");
				predicates.add(cb.or(ten, ma));
			}
			if (Objects.nonNull(trangThai)) {
				predicates.add(cb.equal(root.<String>get("trangThai"), trangThai));
			}
			if (StringUtils.isNotBlank(appCode)) {
				predicates.add(cb.equal(cb.lower(root.get("appCode")), appCode.toLowerCase()));
			}
			if (CollUtil.isNotEmpty(maUngDungs)) {
				Expression<String> expression = root.get("maUngDung");
				Predicate inList = expression.in(maUngDungs);
				predicates.add(inList);
			}
			
			return cb.and(predicates.toArray(new Predicate[]{}));
		};
	}
}
