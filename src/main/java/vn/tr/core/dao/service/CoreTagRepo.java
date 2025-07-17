package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreTag;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CoreTagRepo extends JpaRepository<CoreTag, Long>, JpaSpecificationExecutor<CoreTag> {
	
	boolean existsByIdNotAndCodeIgnoreCase(long id, String code);
	
	boolean existsByIdNotAndNameIgnoreCase(long id, String name);
	
	boolean existsByCodeIgnoreCase(String code);
	
	boolean existsByNameIgnoreCase(String name);
	
	boolean existsById(long id);
	
	Optional<CoreTag> findFirstByCodeIgnoreCase(String code);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CoreTag g SET g.deletedAt = CURRENT_TIMESTAMP WHERE g.id IN :ids")
	void softDeleteByIds(@Param("ids") Set<Long> ids);
}
