package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreMenu;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CoreMenuRepo extends JpaRepository<CoreMenu, Long>, JpaSpecificationExecutor<CoreMenu> {
	
	@Modifying(clearAutomatically = true)
	@Query("update CoreMenu u set u.daXoa = ?1 where u.isReload = TRUE AND u.appCode = ?2")
	void setFixedDaXoaAndAppCode(boolean daXoa, String appCode);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CoreModule g SET g.deletedAt = CURRENT_TIMESTAMP WHERE g.id IN :ids")
	void softDeleteByIds(@Param("ids") Set<Long> ids);
	
	Optional<CoreMenu> findFirstByCodeIgnoreCaseAndAppCodeIgnoreCase(String code, String appCode);

//	@Query(
//			value = "SELECT DISTINCT m.ma FROM core_menu m " +
//					" LEFT JOIN core_role2menu r2m ON m.id = r2m.menu_id AND r2m.daxoa = FALSE" +
//					" LEFT JOIN core_role r ON r2m.role_id = r.id AND r.trangthai = TRUE AND r.daxoa = FALSE" +
//					" LEFT JOIN core_user_role u2r ON r.id = u2r.role_id AND u2r.daxoa = FALSE AND u2r." +
//					" WHERE m. = TRUE AND m.daxoa = FALSE",
//			nativeQuery = true
//	)
//	Set<String> getMenuMaByUserName(String userName);

}
