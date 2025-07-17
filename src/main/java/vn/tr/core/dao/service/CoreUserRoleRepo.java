package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreUserRole;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CoreUserRoleRepo extends JpaRepository<CoreUserRole, Long>, JpaSpecificationExecutor<CoreUserRole> {
	
	List<CoreUserRole> findByUsernameIgnoreCase(String username);
	
	@Query("SELECT cur FROM CoreUserRole cur WHERE cur.username = :username AND cur.appCode = :appCode")
	List<CoreUserRole> findAllByUsernameAndAppCodeIncludingDeleted(@Param("username") String username, @Param("appCode") String appCode);
	
	@Query("SELECT cur.roleCode FROM CoreUserRole cur WHERE cur.username = :username")
	Set<String> findRoleCodesByUsername(@Param("username") String username);
	
	Optional<CoreUserRole> findFirstByUsernameAndAppCodeAndRoleCode(String username, String appCode, String roleCode);
	
}
