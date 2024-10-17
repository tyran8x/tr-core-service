package vn.tr.core.dao.service;

import vn.tr.core.dao.model.CoreUser2Group;

import java.util.List;
import java.util.Optional;

public interface CoreUser2GroupService {

	CoreUser2Group save(CoreUser2Group coreUser2Group);

	void deleteById(Long id);

	boolean existsById(Long id);

	Optional<CoreUser2Group> findById(Long id);

	List<CoreUser2Group> findByGroupIdAndDaXoaFalse(Long groupId);

	Optional<CoreUser2Group> findFirstByGroupIdAndUserName(Long groupId, String userName);

	void setFixedDaXoaForGroupId(boolean daXoa, Long groupId);

	void setFixedDaXoaForUserName(boolean daXoa, String userName);

	List<CoreUser2Group> findByUserNameAndDaXoaFalse(String userName);

}
