package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreUserGroup;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoreUserGroupRepo extends JpaRepository<CoreUserGroup, Long>, JpaSpecificationExecutor<CoreUserGroup> {
	
	List<CoreUserGroup> findByGroupIdAndDaXoaFalse(Long groupId);
	
	Optional<CoreUserGroup> findFirstByGroupIdAndUserName(Long groupId, String userName);
	
	@Modifying(clearAutomatically = true)
	@Query("update CoreUserGroup u set u.daXoa = ?1 where u.groupId = ?2")
	void setFixedDaXoaForGroupId(boolean daXoa, Long groupId);
	
	@Modifying(clearAutomatically = true)
	@Query("update CoreUserGroup u set u.daXoa = ?1 where u.userName = ?2")
	void setFixedDaXoaForUserName(boolean daXoa, String userName);
	
	List<CoreUserGroup> findByUserNameAndDaXoaFalse(String userName);
}
