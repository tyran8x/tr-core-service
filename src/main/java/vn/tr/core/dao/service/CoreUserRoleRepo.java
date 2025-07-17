package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreUserRole;

import java.util.List;

@Repository
public interface CoreUserRoleRepo extends JpaRepository<CoreUserRole, Long>, JpaSpecificationExecutor<CoreUserRole> {
	
	List<CoreUserRole> findByUsernameIgnoreCase(String username);
	
	@Query("SELECT cur FROM CoreUserRole cur WHERE cur.username = :username AND cur.appCode = :appCode")
	List<CoreUserRole> findAllByUsernameAndAppCodeIncludingDeleted(@Param("username") String username, @Param("appCode") String appCode);
	
}
