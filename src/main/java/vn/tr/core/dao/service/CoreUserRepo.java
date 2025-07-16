package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreUser;

import java.util.Optional;

@Repository
public interface CoreUserRepo extends JpaRepository<CoreUser, Long>, JpaSpecificationExecutor<CoreUser> {
	
	boolean existsByUsernameIgnoreCase(String username);
	
	boolean existsByIdNotAndUsernameIgnoreCase(long id, String username);
	
	Optional<CoreUser> findFirstByUsernameIgnoreCase(String username);
	
}
