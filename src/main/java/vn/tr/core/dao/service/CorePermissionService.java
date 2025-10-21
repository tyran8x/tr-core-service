package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.tr.core.dao.model.CorePermission;
import vn.tr.core.data.criteria.CorePermissionSearchCriteria;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CorePermissionService {
	
	Optional<CorePermission> findById(Long id);
	
	CorePermission save(CorePermission corePermission);
	
	void delete(Long id);
	
	boolean existsById(Long id);
	
	void saveAll(Iterable<CorePermission> corePermissions);
	
	Set<String> findAllCodesByAppCode(String appCode);
	
	List<CorePermission> findAllByIds(Collection<Long> ids);
	
	List<CorePermission> findAllByAppCode(String appCode);
	
	Set<String> findAllCodesByUsernameAndAppCode(String username, String appCode);
	
	Set<String> findAllCodesByUsernameAndAppCodes(String username, Set<String> appCodes);
	
	boolean isSuperAdmin(String username);
	
	void deleteByIds(Collection<Long> ids);
	
	Page<CorePermission> findAll(CorePermissionSearchCriteria criteria, Pageable pageable);
	
	List<CorePermission> findAll(CorePermissionSearchCriteria criteria);
	
	Optional<CorePermission> findByCodeAndAppCodeIncludingDeleted(String code, String appCode);
	
	boolean isModuleInUse(Long moduleId);
	
	JpaRepository<CorePermission, Long> getRepository();
	
	boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode);
	
	boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode);
	
	boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode);
	
	boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode);
	
	List<CorePermission> findAllByAppCodeAndCodeIn(String appCode, Collection<String> codes);
	
}
