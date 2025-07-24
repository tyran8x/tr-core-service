package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.tr.core.dao.model.CoreModule;
import vn.tr.core.data.criteria.CoreModuleSearchCriteria;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CoreModuleService {
	
	Optional<CoreModule> findById(Long id);
	
	CoreModule save(CoreModule coreModule);
	
	void delete(Long id);
	
	boolean existsById(Long id);
	
	Page<CoreModule> findAll(CoreModuleSearchCriteria coreModuleSearchCriteria, Pageable pageable);
	
	List<CoreModule> findAll(CoreModuleSearchCriteria coreModuleSearchCriteria);
	
	boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode);
	
	boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode);
	
	boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode);
	
	boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode);
	
	boolean existsByIdAndAppCode(long id, String appCode);
	
	List<CoreModule> findAllByIds(Collection<Long> ids);
	
	void deleteByIds(Collection<Long> ids);
	
	Optional<CoreModule> findByCodeAndAppCodeIncludingDeleted(String code, String appCode);
	
	JpaRepository<CoreModule, Long> getRepository();
	
	List<CoreModule> findAllByAppCode(String appCode);
	
	/**
	 * BỔ SUNG: Lưu một danh sách các module.
	 */
	List<CoreModule> saveAll(Iterable<CoreModule> modules);
	
}
