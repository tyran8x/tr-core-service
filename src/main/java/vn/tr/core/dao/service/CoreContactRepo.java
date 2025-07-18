package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.core.dao.model.CoreContact;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CoreContactRepo extends JpaRepository<CoreContact, Long>, JpaSpecificationExecutor<CoreContact> {
	
	boolean existsById(long id);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CoreContact g SET g.deletedAt = CURRENT_TIMESTAMP WHERE g.id IN :ids")
	void softDeleteByIds(@Param("ids") Set<Long> ids);
	
	@Query("SELECT c FROM CoreContact c WHERE c.ownerType = :ownerType AND c.ownerValue = :ownerValue")
	List<CoreContact> findAllByOwnerIncludingDeleted(@Param("ownerType") String ownerType, @Param("ownerValue") String ownerValue);
	
	List<CoreContact> findByOwnerTypeAndOwnerValueAndStatus(String ownerType, String ownerValue, LifecycleStatus status);
	
	@Query("SELECT c FROM CoreContact c WHERE c.ownerType = :ownerType AND c.ownerValue = :ownerValue AND c.contactType = 'EMAIL' AND c.isPrimary = true")
	Optional<CoreContact> findPrimaryEmailByOwner(@Param("ownerType") String ownerType, @Param("ownerValue") String ownerValue);
	
	void deleteAllByOwnerTypeAndOwnerValue(String ownerType, String ownerValue);
	
}
