package vn.tr.core.dao.service;

import vn.tr.core.dao.model.CoreUserApp;

import java.util.Optional;
import java.util.Set;

public interface CoreUserAppService {
	
	void deleteById(Long id);
	
	boolean existsById(Long id);
	
	Optional<CoreUserApp> findById(Long id);
	
	CoreUserApp save(CoreUserApp coreUserApp);
	
	void synchronizeUserApps(String username, Set<String> newAppCodes);
	
	Optional<CoreUserApp> findByUsernameAndAppCode(String username, String appCode);
	
	Set<String> findActiveAppCodesByUsername(String username);
	
	void assignUserToApp(String username, String appCode, String defaultUserType);
	
}
