package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreRole;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CoreRoleRepo extends JpaRepository<CoreRole, Long>, JpaSpecificationExecutor<CoreRole> {
	
	boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, @Nullable String appCode);
	
	boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, @Nullable String appCode);
	
	boolean existsByCodeIgnoreCaseAndAppCode(String code, @Nullable String appCode);
	
	boolean existsByNameIgnoreCaseAndAppCode(String name, @Nullable String appCode);
	
	boolean existsByIdAndAppCode(long id, @Nullable String appCode);
	
	boolean existsByCodeIgnoreCase(String code);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CoreRole g SET g.deletedAt = CURRENT_TIMESTAMP WHERE g.id IN :ids")
	void softDeleteByIds(@Param("ids") Set<Long> ids);
	
	Optional<CoreRole> findFirstByAppCodeAndCodeIgnoreCase(String appCode, String code);
	
	@Query("SELECT r FROM CoreRole r WHERE r.code = :code AND r.appCode = :appCode")
	Optional<CoreRole> findByCodeAndAppCodeEvenIfDeleted(@Param("code") String code, @Param("appCode") String appCode);
	
}
