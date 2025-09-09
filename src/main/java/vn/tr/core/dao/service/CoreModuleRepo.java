package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreModule;

import java.util.Collection;
import java.util.List;

@Repository
public interface CoreModuleRepo extends JpaRepository<CoreModule, Long>, JpaSpecificationExecutor<CoreModule> {
	
	boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, @Nullable String appCode);
	
	boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, @Nullable String appCode);
	
	boolean existsByCodeIgnoreCaseAndAppCode(String code, @Nullable String appCode);
	
	boolean existsByNameIgnoreCaseAndAppCode(String name, @Nullable String appCode);
	
	boolean existsByIdAndAppCode(long id, @Nullable String appCode);
	
	List<CoreModule> findAllByAppCode(String appCode);
	
	@Query("SELECT m FROM CoreModule m WHERE m.code = :code AND m.appCode = :appCode ORDER BY m.deletedAt ASC NULLS FIRST, m.updatedAt DESC")
	List<CoreModule> findAllByCodeAndAppCodeIncludingDeletedSorted(@Param("code") String code, @Param("appCode") String appCode);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CoreModule m SET m.deletedAt = CURRENT_TIMESTAMP WHERE m.id IN :ids")
	void softDeleteByIds(@Param("ids") Collection<Long> ids);
	
}
