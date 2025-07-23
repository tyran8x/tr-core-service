package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.tr.core.dao.model.CoreRole;
import vn.tr.core.data.criteria.CoreRoleSearchCriteria;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CoreRoleService {
	
	Optional<CoreRole> findById(Long id);
	
	CoreRole save(CoreRole coreRole);
	
	void delete(Long id);
	
	boolean existsById(Long id);
	
	Page<CoreRole> findAll(CoreRoleSearchCriteria coreRoleSearchCriteria, Pageable pageable);
	
	List<CoreRole> findAll(CoreRoleSearchCriteria coreRoleSearchCriteria);
	
	boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode);
	
	boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode);
	
	boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode);
	
	boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode);
	
	boolean existsByIdAndAppCode(long id, String appCode);
	
	List<CoreRole> findAllByIds(Collection<Long> ids);
	
	void deleteByIds(Collection<Long> ids);
	
	Optional<CoreRole> findByCodeAndAppCodeIncludingDeleted(String code, String appCode);
	
	JpaRepository<CoreRole, Long> getRepository();
	
}
