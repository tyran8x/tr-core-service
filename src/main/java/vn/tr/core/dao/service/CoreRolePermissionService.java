package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.tr.core.dao.model.CorePermission;
import vn.tr.core.dao.model.CoreRole;
import vn.tr.core.dao.model.CoreRolePermission;

import java.util.*;

public interface CoreRolePermissionService {
	
	void deleteById(Long id);
	
	boolean existsById(Long id);
	
	Optional<CoreRolePermission> findById(Long id);
	
	CoreRolePermission save(CoreRolePermission coreRolePermission);
	
	void refreshRolePermsCache();
	
	void refreshRolePermsCache(String roleMa);
	
	void refreshRolePermsCache(String oldRoleMa, String newRoleMa);
	
	// --- Hỗ trợ AssociationSyncHelper ---
	List<CoreRolePermission> findByRoleCodeAndAppCodeIncludingDeleted(String roleCode, String appCode);
	
	JpaRepository<CoreRolePermission, Long> getRepository();
	
	// --- Truy vấn nghiệp vụ ---
	Set<String> findPermissionCodesByRoleCodesAndAppCode(Collection<String> roleCodes, String appCode);
	
	Set<String> findPermissionCodesByRoleCodesAndAppCodes(Collection<String> roleCodes, Set<String> appCodes);
	
	// --- Kiểm tra ràng buộc ---
	boolean isRoleInUse(CoreRole role);
	
	boolean isPermissionInUse(CorePermission permission);
	
	Map<String, Set<String>> findActivePermissionsForRoles(Set<String> roleCodes, String appCode);
	
	Set<String> findPermissionCodesByRoleCodes(Collection<String> roleCodes);
	
}
