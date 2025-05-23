package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreRole2Menu;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CoreRole2MenuRepo extends JpaRepository<CoreRole2Menu, Long>, JpaSpecificationExecutor<CoreRole2Menu> {
	
	List<CoreRole2Menu> findByDaXoaFalse();
	
	List<CoreRole2Menu> findByMenuIdAndDaXoaFalse(Long menuId);
	
	List<CoreRole2Menu> findByRoleIdAndDaXoaFalse(Long roleId);
	
	Optional<CoreRole2Menu> findFirstByRoleIdAndMenuId(Long roleId, Long menuId);
	
	@Query(
			value = """
					WITH RECURSIVE menu AS (SELECT * FROM core_menu WHERE id IN
					   (SELECT DISTINCT r2m.menu_id FROM core_role2menu r2m
					JOIN core_role r ON r.id = r2m.role_id AND r.daxoa = FALSE AND r.trangthai = TRUE AND r.ma IN ?1 WHERE r2m.daxoa = FALSE
					ORDER BY r2m.menu_id)
					UNION ALL SELECT core_menu.* FROM core_menu JOIN menu ON menu.cha_id = core_menu.id)
					SELECT DISTINCT id FROM menu ORDER BY id
					""", nativeQuery = true
	)
	List<Long> getMenuIds(Set<String> roles);
	
	@Query(
			value = "WITH RECURSIVE menu AS (SELECT * FROM core_menu WHERE id IN " +
					" (SELECT DISTINCT r2m.menu_id FROM core_role2menu r2m " +
					" JOIN core_role r ON r.id = r2m.role_id AND r.daxoa = FALSE AND r.trangthai = TRUE AND r.ma IN ?1 WHERE r2m.daxoa = FALSE " +
					" ORDER BY r2m.menu_id) " +
					" UNION ALL SELECT core_menu.* FROM core_menu JOIN menu ON menu.cha_id = core_menu.id) " +
					" SELECT DISTINCT ma FROM menu ORDER BY ma", nativeQuery = true
	)
	Set<String> getMenuMas(Set<String> roles);
	
	@Modifying(clearAutomatically = true)
	@Query("update CoreRole2Menu u set u.daXoa = ?1 where u.roleId = ?2")
	void setFixedDaXoaForRoleId(boolean daXoa, Long roleId);
}
