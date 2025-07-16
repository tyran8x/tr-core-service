package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreWorkSpaceItem;

import java.util.Set;

@Repository
public interface CoreWorkSpaceItemRepo extends JpaRepository<CoreWorkSpaceItem, Long>, JpaSpecificationExecutor<CoreWorkSpaceItem> {
	
	boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, @Nullable String appCode);
	
	boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, @Nullable String appCode);
	
	boolean existsByCodeIgnoreCaseAndAppCode(String code, @Nullable String appCode);
	
	boolean existsByNameIgnoreCaseAndAppCode(String name, @Nullable String appCode);
	
	boolean existsByIdAndAppCode(long id, @Nullable String appCode);
	
	@Override
	@EntityGraph(attributePaths = {"parent"})
	Page<CoreWorkSpaceItem> findAll(@Nullable Specification<CoreWorkSpaceItem> spec, Pageable pageable);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CoreWorkSpaceItem g SET g.deletedAt = CURRENT_TIMESTAMP WHERE g.id IN :ids")
	void softDeleteByIds(@Param("ids") Set<Long> ids);
	
}
