package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreContact;

import java.util.List;

@Repository
public interface CoreContactRepo extends JpaRepository<CoreContact, Long>, JpaSpecificationExecutor<CoreContact> {
	
	@Query("SELECT c FROM CoreContact c WHERE c.ownerType = :ownerType AND c.ownerValue = :ownerValue AND c.appCode = :appCode")
	List<CoreContact> findAllByOwnerInAppIncludingDeleted(@Param("ownerType") String ownerType, @Param("ownerValue") String ownerValue,
			@Param("appCode") String appCode);
	
	List<CoreContact> findByOwnerTypeAndOwnerValueAndAppCode(String ownerType, String ownerValue, String appCode);
	
}
