package vn.tr.core.dao.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import vn.tr.common.core.utils.StringUtils;
import vn.tr.core.dao.model.CoreRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CoreRoleSpecifications {
	
	private CoreRoleSpecifications() {
	
	}
	
	public static Specification<CoreRole> quickSearch(final String search, final Boolean trangThai, final String appCode) {
		
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.<String>get("daXoa"), false));
			
			if (StringUtils.isNotBlank(search)) {
				Predicate ten = cb.like(cb.lower(root.get("ten")), "%" + search.toLowerCase().trim() + "%");
				Predicate ma = cb.like(cb.lower(root.get("ma")), "%" + search.toLowerCase().trim() + "%");
				predicates.add(cb.or(ten, ma));
			}
			if (StringUtils.isNotBlank(appCode)) {
				predicates.add(cb.equal(root.<String>get("appCode"), appCode));
			}
			if (Objects.nonNull(trangThai)) {
				predicates.add(cb.equal(root.<String>get("trangThai"), trangThai));
			}
			return cb.and(predicates.toArray(new Predicate[]{}));
		};
	}
}
