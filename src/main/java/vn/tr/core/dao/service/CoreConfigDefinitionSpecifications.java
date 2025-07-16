package vn.tr.core.dao.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import vn.tr.core.dao.model.CoreConfigDefinition;

import java.util.ArrayList;
import java.util.List;

class CoreConfigDefinitionSpecifications {
	
	private CoreConfigDefinitionSpecifications() {
	
	}
	
	public static Specification<CoreConfigDefinition> quickSearch(final String maUngDung, final String code, final Boolean trangThai) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.<String>get("daXoa"), false));
			
			if (maUngDung != null && !maUngDung.isEmpty()) {
				predicates.add(cb.equal(root.<String>get("maUngDung"), maUngDung));
			}
			if (code != null && !code.isEmpty()) {
				predicates.add(cb.equal(root.<String>get("code"), code));
			}
			if (trangThai != null) {
				predicates.add(cb.equal(root.<String>get("trangThai"), trangThai));
			}
			return cb.and(predicates.toArray(new Predicate[]{}));
		};
	}
}
