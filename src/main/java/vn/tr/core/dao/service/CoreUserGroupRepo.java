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
	
	List<CoreUserGroup> findByUsernameIgnoreCase(String username);
	
	@Query("SELECT cug.groupCode FROM CoreUserGroup cug WHERE cug.username = :username")
	Set<String> findGroupCodesByUsername(@Param("username") String username);
	
}
