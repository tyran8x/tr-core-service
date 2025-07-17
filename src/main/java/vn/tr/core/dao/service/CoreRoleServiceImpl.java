package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreApp;
import vn.tr.core.dao.model.CoreRole;
import vn.tr.core.data.criteria.CoreRoleSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CoreRoleServiceImpl implements CoreRoleService {
	
	private final CoreRoleRepo repo;
	
	public CoreRoleServiceImpl(CoreRoleRepo repo) {
		this.repo = repo;
	}
	
	@Override
	public Optional<CoreRole> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public CoreRole save(CoreRole coreRole) {
		return repo.save(coreRole);
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
	public Page<CoreRole> findAll(CoreRoleSearchCriteria coreRoleSearchCriteria, Pageable pageable) {
		return repo.findAll(CoreRoleSpecifications.quickSearch(coreRoleSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreRole> findAll(CoreRoleSearchCriteria coreRoleSearchCriteria) {
		return repo.findAll(CoreRoleSpecifications.quickSearch(coreRoleSearchCriteria));
	}
	
	@Override
	public boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode) {
		return repo.existsByIdNotAndCodeIgnoreCaseAndAppCode(id, code, appCode);
	}
	
	@Override
	public boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode) {
		return repo.existsByIdNotAndNameIgnoreCaseAndAppCode(id, name, appCode);
	}
	
	@Override
	public boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode) {
		return repo.existsByCodeIgnoreCaseAndAppCode(code, appCode);
	}
	
	@Override
	public boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode) {
		return repo.existsByNameIgnoreCaseAndAppCode(name, appCode);
	}
	
	@Override
	public boolean existsByIdAndAppCode(long id, String appCode) {
		return repo.existsByIdAndAppCode(id, appCode);
	}
	
	@Override
	public boolean existsByCodeIgnoreCase(String code) {
		return repo.existsByCodeIgnoreCase(code);
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
	@Transactional
	public CoreRole findOrCreate(CoreApp coreApp, String roleCode, String roleName) {
		return repo.findFirstByAppCodeAndCodeIgnoreCase(coreApp.getCode(), roleCode)
				.orElseGet(() -> {
					CoreRole newRole = new CoreRole();
					newRole.setAppCode(coreApp.getCode());
					newRole.setCode(roleCode);
					newRole.setName(roleName);
					return repo.save(newRole);
				});
	}
	
}
