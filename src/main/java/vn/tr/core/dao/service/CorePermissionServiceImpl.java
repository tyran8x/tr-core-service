package vn.tr.core.dao.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CorePermission;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CorePermissionServiceImpl implements CorePermissionService {
	
	private final CorePermissionRepo repo;
	
	public CorePermissionServiceImpl(CorePermissionRepo repo) {
		this.repo = repo;
	}
	
	@Override
	public Optional<CorePermission> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public CorePermission save(CorePermission corePermission) {
		return repo.save(corePermission);
	}
	
	@Override
	public void delete(Long id) {
		repo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Set<Long> ids) {
		if (ids.isEmpty()) {
			return;
		}
		repo.softDeleteByIds(ids);
	}
	
	@Override
	public void saveAll(Iterable<CorePermission> corePermissions) {
		repo.saveAll(corePermissions);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Set<String> findAllCodesByAppCode(String appCode) {
		return repo.findAllCodesByAppCode(appCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CorePermission> findAllByAppCode(String appCode) {
		return repo.findAllByAppCode(appCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Set<String> findAllCodesByUsernameAndAppCode(String username, String appCode) {
		return repo.findAllCodesByUsernameAndAppCode(username.toLowerCase(), appCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean isSuperAdmin(String username) {
		return repo.isSuperAdmin(username.toLowerCase());
	}
	
}
