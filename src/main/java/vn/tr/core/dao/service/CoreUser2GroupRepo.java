package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreUser2Group;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoreUser2GroupRepo extends JpaRepository<CoreUser2Group, Long>, JpaSpecificationExecutor<CoreUser2Group> {

	List<CoreUser2Group> findByGroupIdAndDaXoaFalse(Long groupId);

	Optional<CoreUser2Group> findFirstByGroupIdAndUserName(Long groupId, String userName);

	@Modifying(clearAutomatically = true)
	@Query("update CoreUser2Group u set u.daXoa = ?1 where u.groupId = ?2")
	void setFixedDaXoaForGroupId(boolean daXoa, Long groupId);

	@Modifying(clearAutomatically = true)
	@Query("update CoreUser2Group u set u.daXoa = ?1 where u.userName = ?2")
	void setFixedDaXoaForUserName(boolean daXoa, String userName);

	List<CoreUser2Group> findByUserNameAndDaXoaFalse(String userName);
}
