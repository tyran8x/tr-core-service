package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tr.core.dao.model.CoreUserConnect;

import java.util.List;
import java.util.Optional;

public interface CoreUserConnectService {
	
	CoreUserConnect save(CoreUserConnect coreUserConnect);
	
	void deleteById(Long id);
	
	Optional<CoreUserConnect> findById(Long id);
	
	Page<CoreUserConnect> findAll(String search, Boolean trangThai, List<String> maUngDungs, String appCode, Pageable pageable);
	
	Optional<CoreUserConnect> findByUserNameIgnoreCaseAndAppNameAndDaXoaFalse(String userName, String appName);
	
	List<CoreUserConnect> findByUserNameIgnoreCaseAndDaXoaFalse(String userName);
}
