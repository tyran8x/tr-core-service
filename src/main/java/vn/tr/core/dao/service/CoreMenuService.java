package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tr.core.dao.model.CoreMenu;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CoreMenuService {
	
	void delete(Long id);
	
	boolean existsById(Long id);
	
	Page<CoreMenu> findAll(String search, Boolean trangThai, String appCode, Pageable pageable);
	
	List<CoreMenu> findByDaXoaFalse();
	
	Optional<CoreMenu> findById(Long id);
	
	Optional<CoreMenu> findByIdAndDaXoaFalse(Long id);
	
	List<CoreMenu> findByIdInAndDaXoaFalse(List<Long> ids);
	
	List<CoreMenu> findByIdInAndTrangThaiTrueAndAppCodeAndDaXoaFalse(List<Long> ids, String appCode);
	
	List<CoreMenu> findByTrangThaiTrueAndAppCodeAndDaXoaFalse(String appCode);
	
	Optional<CoreMenu> findFirstByMaIgnoreCaseAndAppCodeIgnoreCase(String ma, String appCode);
	
	CoreMenu save(CoreMenu coreMenu);
	
	void setFixedDaXoa(boolean daXoa);
	
	void setFixedDaXoaAndAppCode(boolean daXoa, String appCode);
	
	void deleteByIds(Set<Long> ids);
	
}
