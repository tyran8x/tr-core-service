package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreUserApp;

import java.util.List;
import java.util.Set;

@Repository
public interface CoreUserAppRepo extends JpaRepository<CoreUserApp, Long>, JpaSpecificationExecutor<CoreUserApp> {
	
	@Query("SELECT cua FROM CoreUserApp cua WHERE cua.username = :username")
	List<CoreUserApp> findAllByUsernameIncludingDeleted(@Param("username") String username);
	
	@Query("SELECT cua FROM CoreUserApp cua WHERE cua.username = :username AND cua.appCode = :appCode")
	List<CoreUserApp> findAllByUsernameAndAppCodeIncludingDeleted(@Param("username") String username, @Param("appCode") String appCode);
	
	@Query("SELECT cua.appCode FROM CoreUserApp cua WHERE cua.username = :username")
	Set<String> findActiveAppCodesByUsername(@Param("username") String username);
	
	List<CoreUserApp> findByUsernameAndAppCode(String username, String appCode);
	
}
