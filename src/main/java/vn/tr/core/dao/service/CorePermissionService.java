package vn.tr.core.dao.service;

import vn.tr.core.dao.model.CorePermission;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CorePermissionService {
	
	Optional<CorePermission> findById(Long id);
	
	CorePermission save(CorePermission corePermission);
	
	void delete(Long id);
	
	boolean existsById(Long id);
	
	void deleteByIds(Set<Long> ids);
	
	void saveAll(Iterable<CorePermission> corePermissions);
	
	Set<String> findAllCodesByAppCode(String appCode);
	
	List<CorePermission> findAllByAppCode(String appCode);
	
	Set<String> findAllCodesByUsernameAndAppCode(String username, String appCode);
	
	boolean isSuperAdmin(String username);
	
}
