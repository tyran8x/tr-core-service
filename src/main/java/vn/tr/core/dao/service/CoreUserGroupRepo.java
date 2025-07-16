package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreUserGroup;

import java.util.List;

@Repository
public interface CoreUserGroupRepo extends JpaRepository<CoreUserGroup, Long>, JpaSpecificationExecutor<CoreUserGroup> {
	
	List<CoreUserGroup> findByUsernameIgnoreCase(String username);
	
}
