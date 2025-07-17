package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreUserType;

import java.util.Set;

@Repository
public interface CoreUserTypeRepo extends JpaRepository<CoreUserType, Long>, JpaSpecificationExecutor<CoreUserType> {
	
	boolean existsByIdNotAndCodeIgnoreCase(long id, String code);
	
	boolean existsByIdNotAndNameIgnoreCase(long id, String name);
	
	boolean existsByCodeIgnoreCase(String code);
	
	boolean existsByNameIgnoreCase(String name);
	
	boolean existsById(long id);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CoreUserType g SET g.deletedAt = CURRENT_TIMESTAMP WHERE g.id IN :ids")
	void softDeleteByIds(@Param("ids") Set<Long> ids);
}
