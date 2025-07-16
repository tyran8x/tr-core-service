package vn.tr.core.dao.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreUserRole;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CoreUserRoleServiceImpl implements CoreUserRoleService {
	
	private final CoreUserRoleRepo repo;
	
	public CoreUserRoleServiceImpl(CoreUserRoleRepo repo) {
		this.repo = repo;
	}
	
	@Override
	public CoreUserRole save(CoreUserRole coreUserRole) {
		return repo.save(coreUserRole);
	}
	
	@Override
	public void deleteById(Long id) {
		repo.deleteById(id);
	}
	
	@Override
	public Optional<CoreUserRole> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public List<CoreUserRole> findByRoleAndDaXoaFalse(String role) {
		return repo.findByRoleAndDaXoaFalse(role);
	}
	
	@Override
	public Optional<CoreUserRole> findFirstByRoleAndUserName(String role, String userName) {
		return repo.findFirstByRoleAndUserName(role, userName);
	}
	
	@Override
	public void setFixedDaXoaForRole(boolean daXoa, String role) {
		repo.setFixedDaXoaForRole(daXoa, role);
	}
	
	@Override
	public void setFixedDaXoaForUserName(boolean daXoa, String userName) {
		repo.setFixedDaXoaForUserName(daXoa, userName);
	}
	
	@Override
	public List<CoreUserRole> findByUserNameAndDaXoaFalse(String userName) {
		return repo.findByUserNameAndDaXoaFalse(userName);
	}
	
	@Override
	public List<String> getRoleByUserName(String userName) {
		return repo.getRoleByUserName(userName);
	}
	
	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}
	
}
