package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreUserApp;

import java.util.List;

@Repository
public interface CoreUserAppRepo extends JpaRepository<CoreUserApp, Long>, JpaSpecificationExecutor<CoreUserApp> {
	
	List<CoreUserApp> findByUsername(String username);
	
}
