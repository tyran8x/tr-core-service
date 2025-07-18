package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreModule;

import java.util.List;
import java.util.Set;

@Repository
public interface CoreModuleRepo extends JpaRepository<CoreModule, Long>, JpaSpecificationExecutor<CoreModule> {
	
	boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, @Nullable String appCode);
	
	boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, @Nullable String appCode);
	
	boolean existsByCodeIgnoreCaseAndAppCode(String code, @Nullable String appCode);
	
	boolean existsByNameIgnoreCaseAndAppCode(String name, @Nullable String appCode);
	
	boolean existsByIdAndAppCode(long id, @Nullable String appCode);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CoreModule g SET g.deletedAt = CURRENT_TIMESTAMP WHERE g.id IN :ids")
	void softDeleteByIds(@Param("ids") Set<Long> ids);
	
	@Query("SELECT p.code FROM CoreModule p WHERE p.appCode = :appCode")
	Set<String> findAllCodesByAppCode(@Param("appCode") String appCode);
	
	List<CoreModule> findByAppCodeIgnoreCaseAndCodeIgnoreCase(String appCode, String code);
	
	List<CoreModule> findAllByAppCode(String appCode);
	
}
