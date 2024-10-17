package vn.tr.core.dao.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreUser2Group;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CoreUser2GroupServiceImpl implements CoreUser2GroupService {

	private final CoreUser2GroupRepo repo;

	public CoreUser2GroupServiceImpl(CoreUser2GroupRepo repo) {
		this.repo = repo;
	}

	@Override
	public CoreUser2Group save(CoreUser2Group coreUser2Group) {
		return repo.save(coreUser2Group);
	}

	@Override
	public void deleteById(Long id) {
		repo.deleteById(id);
	}

	@Override
	public Optional<CoreUser2Group> findById(Long id) {
		return repo.findById(id);
	}

	@Override
	public List<CoreUser2Group> findByGroupIdAndDaXoaFalse(Long groupId) {
		return repo.findByGroupIdAndDaXoaFalse(groupId);
	}

	@Override
	public Optional<CoreUser2Group> findFirstByGroupIdAndUserName(Long groupId, String userName) {
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
	public List<CoreUser2Group> findByUserNameAndDaXoaFalse(String userName) {
		return repo.findByUserNameAndDaXoaFalse(userName);
	}

	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}

}
