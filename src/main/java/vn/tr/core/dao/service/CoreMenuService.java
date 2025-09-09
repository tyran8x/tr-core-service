package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tr.core.dao.model.CoreMenu;
import vn.tr.core.data.criteria.CoreMenuSearchCriteria;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CoreMenuService {
	
	void delete(Long id);
	
	boolean existsById(Long id);
	
	Page<CoreMenu> findAll(CoreMenuSearchCriteria coreMenuSearchCriteria, Pageable pageable);
	
	List<CoreMenu> findAll(CoreMenuSearchCriteria coreMenuSearchCriteria);
	
	Optional<CoreMenu> findById(Long id);
	
	CoreMenu save(CoreMenu coreMenu);
	
	void deleteByIds(Collection<Long> ids);
	
	Optional<CoreMenu> findByCodeSafely(String appCode, String code);
	
	List<CoreMenu> findAllByAppCodeIncludingDeleted(String appCode);
	
	List<CoreMenu> findAllByAppCode(String appCode);
	
	boolean hasChildren(Long menuId);
	
	void saveAll(Iterable<CoreMenu> menus);
	
	void softDeleteAll(List<CoreMenu> menusToDelete);
	
	void softDeleteAllByAppCode(String appCode);
	
}
