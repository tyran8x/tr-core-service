package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.tr.core.dao.model.CoreGroup;
import vn.tr.core.data.criteria.CoreGroupSearchCriteria;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Interface cho Service Layer của CoreGroup. Định nghĩa các nghiệp vụ cơ bản và tái sử dụng liên quan đến thực thể CoreGroup.
 *
 * @author tyran8x
 * @version 2.2
 */
public interface CoreGroupService {
	
	Optional<CoreGroup> findById(Long id);
	
	List<CoreGroup> findAllByIds(Collection<Long> ids);
	
	CoreGroup save(CoreGroup coreGroup);
	
	void delete(Long id);
	
	void deleteByIds(Collection<Long> ids);
	
	Page<CoreGroup> findAll(CoreGroupSearchCriteria coreGroupSearchCriteria, Pageable pageable);
	
	List<CoreGroup> findAll(CoreGroupSearchCriteria coreGroupSearchCriteria);
	
	List<CoreGroup> findAllByAppCodeAndCodeIn(String appCode, Collection<String> codes);
	
	Optional<CoreGroup> findByCodeAndAppCodeIncludingDeleted(String code, String appCode);
	
	JpaRepository<CoreGroup, Long> getRepository();
	
	boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode);
	
	boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode);
	
	boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode);
	
	boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode);
	
	boolean existsByIdAndAppCode(long id, String appCode);
	
	Set<String> filterExistingGroupCodesInApp(String appCode, Collection<String> groupCodes);
}
