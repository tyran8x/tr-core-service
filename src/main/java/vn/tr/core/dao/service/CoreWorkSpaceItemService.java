package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tr.core.dao.model.CoreWorkSpaceItem;
import vn.tr.core.data.criteria.CoreWorkSpaceItemSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CoreWorkSpaceItemService {
	
	Optional<CoreWorkSpaceItem> findById(Long id);
	
	CoreWorkSpaceItem save(CoreWorkSpaceItem coreWorkSpaceItem);
	
	void delete(Long id);
	
	boolean existsById(Long id);
	
	Page<CoreWorkSpaceItem> findAll(CoreWorkSpaceItemSearchCriteria coreWorkSpaceItemSearchCriteria, Pageable pageable);
	
	List<CoreWorkSpaceItem> findAll(CoreWorkSpaceItemSearchCriteria coreWorkSpaceItemSearchCriteria);
	
	boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode);
	
	boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode);
	
	boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode);
	
	boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode);
	
	boolean existsByIdAndAppCode(long id, String appCode);
	
	void deleteByIds(Set<Long> ids);
	
}
