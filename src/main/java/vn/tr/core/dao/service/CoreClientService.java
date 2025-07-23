package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.tr.core.dao.model.CoreClient;
import vn.tr.core.data.criteria.CoreClientSearchCriteria;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CoreClientService {
	
	Optional<CoreClient> findById(Long id);
	
	List<CoreClient> findAllByIds(Collection<Long> ids);
	
	CoreClient save(CoreClient coreClient);
	
	void deleteByIds(Collection<Long> ids);
	
	Page<CoreClient> findAll(CoreClientSearchCriteria criteria, Pageable pageable);
	
	Optional<CoreClient> findByClientIdAndAppCodeIncludingDeleted(String clientId, String appCode);
	
	boolean existsByIdNotAndClientIdAndAppCode(Long id, String clientId, String appCode);
	
	boolean existsByClientIdAndAppCode(String clientId, String appCode);
	
	JpaRepository<CoreClient, Long> getRepository();
	
}
