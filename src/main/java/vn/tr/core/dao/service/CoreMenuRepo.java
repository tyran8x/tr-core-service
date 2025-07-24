package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreMenu;

import java.util.Collection;
import java.util.List;

@Repository
public interface CoreMenuRepo extends JpaRepository<CoreMenu, Long>, JpaSpecificationExecutor<CoreMenu> {
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CoreModule g SET g.deletedAt = CURRENT_TIMESTAMP WHERE g.id IN :ids")
	void softDeleteByIds(@Param("ids") Collection<Long> ids);
	
	List<CoreMenu> findByAppCodeIgnoreCaseAndCodeIgnoreCase(String appCode, String code);
	
	@Query("SELECT m FROM CoreMenu m WHERE m.appCode = :appCode")
	List<CoreMenu> findAllByAppCodeIncludingDeleted(@Param("appCode") String appCode);
	
	List<CoreMenu> findAllByAppCode(String appCode);
	
	boolean existsByParentId(Long parentId);
	
}
