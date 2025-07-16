package vn.tr.core.dao.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreUserGroup;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CoreUserGroupServiceImpl implements CoreUserGroupService {
	
	private final CoreUserGroupRepo repo;
	
	public CoreUserGroupServiceImpl(CoreUserGroupRepo repo) {
		this.repo = repo;
	}
	
	@Override
	public CoreUserGroup save(CoreUserGroup coreUser2Group) {
		return repo.save(coreUser2Group);
	}
	
	@Override
	public void deleteById(Long id) {
		repo.deleteById(id);
	}
	
	@Override
	public Optional<CoreUserGroup> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public List<CoreUserGroup> findByGroupIdAndDaXoaFalse(Long groupId) {
		return repo.findByGroupIdAndDaXoaFalse(groupId);
	}
	
	@Override
	public Optional<CoreUserGroup> findFirstByGroupIdAndUserName(Long groupId, String userName) {
		return repo.findFirstByGroupIdAndUserName(groupId, userName);
	}
	
	@Override
	public void setFixedDaXoaForGroupId(boolean daXoa, Long groupId) {
		repo.setFixedDaXoaForGroupId(daXoa, groupId);
	}
	
	@Override
	public void setFixedDaXoaForUserName(boolean daXoa, String userName) {
		repo.setFixedDaXoaForUserName(daXoa, userName);
	}
	
	@Override
	public List<CoreUserGroup> findByUserNameAndDaXoaFalse(String userName) {
		return repo.findByUserNameAndDaXoaFalse(userName);
	}
	
	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}
	
}
