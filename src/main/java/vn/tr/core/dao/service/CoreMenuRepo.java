package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreMenu;

import java.util.List;
import java.util.Set;

@Repository
public interface CoreMenuRepo extends JpaRepository<CoreMenu, Long>, JpaSpecificationExecutor<CoreMenu> {
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CoreModule g SET g.deletedAt = CURRENT_TIMESTAMP WHERE g.id IN :ids")
	void softDeleteByIds(@Param("ids") Set<Long> ids);
	
	List<CoreMenu> findByAppCodeIgnoreCaseAndCodeIgnoreCase(String appCode, String code);
	
	@Query("SELECT m FROM CoreMenu m WHERE m.appCode = :appCode")
	List<CoreMenu> findAllByAppCodeIncludingDeleted(@Param("appCode") String appCode);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CoreMenu m SET m.isPendingDeletion = true WHERE m.appCode = :appCode AND m.deletedAt IS NULL")
	void markAllAsPendingDeletionForApp(@Param("appCode") String appCode);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CoreMenu m SET m.deletedAt = CURRENT_TIMESTAMP WHERE m.isPendingDeletion = true AND m.appCode = :appCode AND m.deletedAt IS NULL")
	int softDeletePendingMenusForApp(@Param("appCode") String appCode);
	
	List<CoreMenu> findAllByAppCode(String appCode);
	
	boolean existsByParentId(Long parentId);
	
}
