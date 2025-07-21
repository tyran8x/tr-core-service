package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreUser;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CoreUserRepo extends JpaRepository<CoreUser, Long>, JpaSpecificationExecutor<CoreUser> {
	
	boolean existsByEmailIgnoreCase(String email);
	
	boolean existsByUsernameIgnoreCase(String username);
	
	boolean existsByIdNotAndUsernameIgnoreCase(long id, String username);
	
	Optional<CoreUser> findFirstByUsernameIgnoreCase(String username);
	
	Optional<CoreUser> findFirstByEmailIgnoreCase(String email);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE CoreWorkSpaceItem g SET g.deletedAt = CURRENT_TIMESTAMP WHERE g.id IN :ids")
	void softDeleteByIds(@Param("ids") Set<Long> ids);
	
	@Query("SELECT u FROM CoreUser u WHERE u.id = :id")
	Optional<CoreUser> findByIdEvenIfDeleted(@Param("id") Long id);
	
}
