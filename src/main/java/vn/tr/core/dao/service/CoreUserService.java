package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.tr.common.core.domain.model.LoginUser;
import vn.tr.common.core.enums.LoginType;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.dao.model.CoreUserApp;
import vn.tr.core.data.criteria.CoreUserSearchCriteria;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public interface CoreUserService {
	
	void deleteById(Long id);
	
	Optional<CoreUser> findById(Long id);
	
	CoreUser save(CoreUser coreUser);
	
	void delete(Long id);
	
	boolean existsById(Long id);
	
	void deleteByIds(Set<Long> ids);
	
	Page<CoreUser> findAll(CoreUserSearchCriteria coreUserSearchCriteria, Pageable pageable);
	
	List<CoreUser> findAll(CoreUserSearchCriteria coreUserSearchCriteria);
	
	boolean existsByUsernameIgnoreCase(String username);
	
	boolean existsByEmailIgnoreCase(String email);
	
	boolean existsByIdNotAndUsernameIgnoreCase(long id, String username);
	
	Optional<CoreUser> findFirstByUsernameIgnoreCase(String username);
	
	Optional<CoreUser> findFirstByEmailIgnoreCase(String email);
	
	void recordLoginInfo(String userName, String status, String message);
	
	LoginUser buildLoginUser(CoreUser user, CoreUserApp userAppAccess);
	
	void checkUserAppStatus(CoreUserApp coreUserApp);
	
	void checkLogin(LoginType loginType, String userName, Supplier<Boolean> supplier);
	
	void logout();
	
	List<CoreUser> findAllByIdIn(Collection<Long> ids);
	
	void deleteByIds(Collection<Long> ids);
	
	Optional<CoreUser> findByUsernameIgnoreCase(String username);
	
	Optional<CoreUser> findByUsernameIgnoreCaseIncludingDeleted(String username);
	
	JpaRepository<CoreUser, Long> getRepository();
	
}
