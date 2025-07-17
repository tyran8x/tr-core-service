package vn.tr.core.dao.service;

import vn.tr.core.dao.model.CoreUserApp;

import java.util.Optional;
import java.util.Set;

public interface CoreUserAppService {
	
	void deleteById(Long id);
	
	boolean existsById(Long id);
	
	Optional<CoreUserApp> findById(Long id);
	
	CoreUserApp save(CoreUserApp coreUserApp);
	
	void replaceUserApps(String username, Set<String> newAppCodes);
	
	Set<String> findAppCodesByUsername(String username);
	
	boolean existsByUsernameAndAppCode(String username, String appCode);
	
	void assignUserToAppIfNotExists(String username, String appCode);
	
}
