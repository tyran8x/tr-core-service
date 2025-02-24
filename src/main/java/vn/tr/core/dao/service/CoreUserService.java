package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tr.common.core.domain.model.LoginUser;
import vn.tr.common.core.enums.LoginType;
import vn.tr.core.dao.model.CoreUser;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public interface CoreUserService {
	
	Optional<CoreUser> findById(Long id);
	
	CoreUser save(CoreUser coreUser);
	
	void delete(Long id);
	
	boolean existsById(Long id);
	
	Page<CoreUser> findAll(String email, String name, List<String> roles, String appCode, Pageable pageable);
	
	boolean existsByEmailIgnoreCaseAndDaXoaFalse(String email);
	
	boolean existsByIdNotAndEmailIgnoreCaseAndDaXoaFalse(long id, String email);
	
	Optional<CoreUser> findFirstByEmailAndDaXoaFalse(String email);
	
	void recordLoginInfo(String userName, String status, String message);
	
	LoginUser buildLoginUser(CoreUser coreUser);
	
	void checkLogin(LoginType loginType, String userName, Supplier<Boolean> supplier);
	
	void logout();
}
