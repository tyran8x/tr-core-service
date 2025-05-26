package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreUserConnect;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoreUserConnectRepo extends JpaRepository<CoreUserConnect, Long>, JpaSpecificationExecutor<CoreUserConnect> {
	
	Optional<CoreUserConnect> findByUserNameIgnoreCaseAndAppNameAndDaXoaFalse(String userName, String appName);
	
	List<CoreUserConnect> findByUserNameIgnoreCaseAndDaXoaFalse(String userName);
	
}
