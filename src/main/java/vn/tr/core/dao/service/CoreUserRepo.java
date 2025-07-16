package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreUser;

import java.util.Optional;

@Repository
public interface CoreUserRepo extends JpaRepository<CoreUser, Long>, JpaSpecificationExecutor<CoreUser> {
	
	boolean existsByUsernameIgnoreCaseAndDaXoaFalse(String username);
	
	boolean existsByIdNotAndUsernameIgnoreCaseAndDaXoaFalse(long id, String username);
	
	Optional<CoreUser> findFirstByUsernameAndDaXoaFalse(String username);
	
}
