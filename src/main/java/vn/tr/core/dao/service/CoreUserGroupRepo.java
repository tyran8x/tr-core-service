package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreUserGroup;

import java.util.List;
import java.util.Set;

@Repository
public interface CoreUserGroupRepo extends JpaRepository<CoreUserGroup, Long>, JpaSpecificationExecutor<CoreUserGroup> {
	
	@Query("SELECT cug FROM CoreUserGroup cug WHERE cug.username = :username AND cug.appCode = :appCode")
	List<CoreUserGroup> findAllByUsernameAndAppCodeIncludingDeleted(@Param("username") String username, @Param("appCode") String appCode);
	
	@Query("SELECT cug.groupCode FROM CoreUserGroup cug WHERE cug.username = :username AND cug.appCode = :appCode")
	Set<String> findActiveGroupCodesByUsernameAndAppCode(@Param("username") String username, @Param("appCode") String appCode);
	
	@Query("SELECT cug.groupCode FROM CoreUserGroup cug WHERE cug.username = :username")
	Set<String> findAllActiveGroupCodesByUsername(@Param("username") String username);
	
}
