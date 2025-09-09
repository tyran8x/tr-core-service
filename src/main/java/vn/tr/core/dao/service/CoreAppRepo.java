package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreApp;

import java.util.Collection;
import java.util.List;

/**
 * Repository interface cho thực thể CoreApp.
 *
 * @author tyran8x
 * @version 2.3
 */
@Repository
public interface CoreAppRepo extends JpaRepository<CoreApp, Long>, JpaSpecificationExecutor<CoreApp> {
	
	boolean existsByIdNotAndCodeIgnoreCase(long id, String code);
	
	boolean existsByIdNotAndNameIgnoreCase(long id, String name);
	
	boolean existsByCodeIgnoreCase(String code);
	
	boolean existsByNameIgnoreCase(String name);
	
	List<CoreApp> findAllByCodeIn(Collection<String> codes);
	
	@Query("SELECT a FROM CoreApp a WHERE a.code = :code ORDER BY a.deletedAt ASC NULLS FIRST, a.updatedAt DESC")
	List<CoreApp> findAllByCodeIgnoreCaseIncludingDeletedSorted(@Param("code") String code);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CoreApp a SET a.deletedAt = CURRENT_TIMESTAMP WHERE a.id IN :ids")
	void softDeleteByIds(@Param("ids") Collection<Long> ids);
}
