package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tr.common.core.domain.model.LoginUser;
import vn.tr.common.core.enums.LoginType;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.data.criteria.CoreUserSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public interface CoreUserService {
	
	Optional<CoreUser> findById(Long id);
	
	CoreUser save(CoreUser coreUser);
	
	void delete(Long id);
	
	boolean existsById(Long id);
	
	Page<CoreUser> findAll(CoreUserSearchCriteria coreUserSearchCriteria, Pageable pageable);
	
	List<CoreUser> findAll(CoreUserSearchCriteria coreUserSearchCriteria);
	
	boolean existsByUsernameIgnoreCase(String username);
	
	boolean existsByIdNotAndUsernameIgnoreCase(long id, String username);
	
	Optional<CoreUser> findFirstByUsernameIgnoreCase(String username);
	
	void recordLoginInfo(String userName, String status, String message);
	
	LoginUser buildLoginUser(CoreUser coreUser);
	
	void checkLogin(LoginType loginType, String userName, Supplier<Boolean> supplier);
	
	void logout();
}
