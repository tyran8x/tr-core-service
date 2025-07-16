package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreRolePermission;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CoreRolePermissionRepo extends JpaRepository<CoreRolePermission, Long>, JpaSpecificationExecutor<CoreRolePermission> {
	
	List<CoreRolePermission> findByDaXoaFalse();
	
	List<CoreRolePermission> findByMenuIdAndDaXoaFalse(Long menuId);
	
	List<CoreRolePermission> findByRoleIdAndDaXoaFalse(Long roleId);
	
	Optional<CoreRolePermission> findFirstByRoleIdAndMenuId(Long roleId, Long menuId);
	
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
	@Query("update CoreRolePermission u set u.daXoa = ?1 where u.roleCode = ?2")
	void setFixedDaXoaForRoleCode(boolean daXoa, String roleCode);
}
