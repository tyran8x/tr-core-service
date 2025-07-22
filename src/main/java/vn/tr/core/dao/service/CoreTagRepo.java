package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreTag;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CoreTagRepo extends JpaRepository<CoreTag, Long>, JpaSpecificationExecutor<CoreTag> {
	
	boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, @Nullable String appCode);
	
	boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, @Nullable String appCode);
	
	boolean existsByCodeIgnoreCaseAndAppCode(String code, @Nullable String appCode);
	
	boolean existsByNameIgnoreCaseAndAppCode(String name, @Nullable String appCode);
	
	boolean existsByIdAndAppCode(long id, @Nullable String appCode);
	
	boolean existsById(long id);
	
	Optional<CoreTag> findFirstByCodeIgnoreCase(String code);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CoreTag g SET g.deletedAt = CURRENT_TIMESTAMP WHERE g.id IN :ids")
	void softDeleteByIds(@Param("ids") Set<Long> ids);
	
	List<CoreTag> findByCodeIn(Set<String> codes);
	
	@Query("SELECT t FROM CoreTag t WHERE t.code = :code")
	List<CoreTag> findAllByCodeEvenIfDeleted(@Param("code") String code);
	
}
