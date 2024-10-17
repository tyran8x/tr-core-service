package vn.tr.core.dao.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreUser2Role;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CoreUser2RoleServiceImpl implements CoreUser2RoleService {

	private final CoreUser2RoleRepo repo;

	public CoreUser2RoleServiceImpl(CoreUser2RoleRepo repo) {
		this.repo = repo;
	}

	@Override
	public CoreUser2Role save(CoreUser2Role coreUser2Role) {
		return repo.save(coreUser2Role);
	}

	@Override
	public void deleteById(Long id) {
		repo.deleteById(id);
	}

	@Override
	public Optional<CoreUser2Role> findById(Long id) {
		return repo.findById(id);
	}

	@Override
	public List<CoreUser2Role> findByRoleAndDaXoaFalse(String role) {
		return repo.findByRoleAndDaXoaFalse(role);
	}

	@Override
	public Optional<CoreUser2Role> findFirstByRoleAndUserName(String role, String userName) {
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
	public List<CoreUser2Role> findByUserNameAndDaXoaFalse(String userName) {
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
