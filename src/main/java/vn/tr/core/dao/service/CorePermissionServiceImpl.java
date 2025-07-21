package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CorePermission;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CorePermissionServiceImpl implements CorePermissionService {
	
	private final CorePermissionRepo corePermissionRepo;
	
	@Override
	public Optional<CorePermission> findById(Long id) {
		return corePermissionRepo.findById(id);
	}
	
	@Override
	public CorePermission save(CorePermission corePermission) {
		return corePermissionRepo.save(corePermission);
	}
	
	@Override
	public void delete(Long id) {
		corePermissionRepo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return corePermissionRepo.existsById(id);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Set<Long> ids) {
		if (ids.isEmpty()) {
			return;
		}
		corePermissionRepo.softDeleteByIds(ids);
	}
	
	@Override
	public void saveAll(Iterable<CorePermission> corePermissions) {
		corePermissionRepo.saveAll(corePermissions);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Set<String> findAllCodesByAppCode(String appCode) {
		return corePermissionRepo.findAllCodesByAppCode(appCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CorePermission> findAllByAppCode(String appCode) {
		return corePermissionRepo.findAllByAppCode(appCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Set<String> findAllCodesByUsernameAndAppCode(String username, String appCode) {
		return corePermissionRepo.findAllCodesByUsernameAndAppCode(username.toLowerCase(), appCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean isSuperAdmin(String username) {
		return corePermissionRepo.isSuperAdmin(username.toLowerCase());
	}
	
}
