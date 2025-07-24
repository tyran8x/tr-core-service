package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.tr.core.dao.model.CoreConfigDefinition;
import vn.tr.core.data.criteria.CoreConfigDefinitionSearchCriteria;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Interface cho Service Layer của CoreConfigDefinition.
 * Định nghĩa các nghiệp vụ cơ bản và tái sử dụng.
 *
 * @author tyran8x
 * @version 2.0
 */
public interface CoreConfigDefinitionService {
	
	Optional<CoreConfigDefinition> findById(Long id);
	
	List<CoreConfigDefinition> findAllByIds(Collection<Long> ids);
	
	CoreConfigDefinition save(CoreConfigDefinition definition);
	
	void deleteByIds(Collection<Long> ids);
	
	Page<CoreConfigDefinition> findAll(CoreConfigDefinitionSearchCriteria criteria, Pageable pageable);
	
	List<CoreConfigDefinition> findAll(CoreConfigDefinitionSearchCriteria criteria);
	
	Optional<CoreConfigDefinition> findByKeyAndAppCode(String key, String appCode);
	
	Optional<CoreConfigDefinition> findByKeyAndAppCodeIncludingDeleted(String key, String appCode);
	
	boolean existsByKeyIgnoreCaseAndAppCode(String key, String appCode);
	
	boolean existsByIdNotAndKeyIgnoreCaseAndAppCode(long id, String key, String appCode);
	
	JpaRepository<CoreConfigDefinition, Long> getRepository();
}
