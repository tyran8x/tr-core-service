package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreGroup;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CoreGroupRepo extends JpaRepository<CoreGroup, Long>, JpaSpecificationExecutor<CoreGroup> {
	
	boolean existsByIdNotAndCodeIgnoreCaseAndAppId(long id, String code, Long appId);
	
	boolean existsByIdNotAndNameIgnoreCaseAndAppId(long id, String name, Long appId);
	
	boolean existsByCodeIgnoreCaseAndAppId(String code, Long appId);
	
	boolean existsByNameIgnoreCaseAndAppId(String name, Long appId);
	
	boolean existsByIdAndAppId(long id, Long appId);
	
	Optional<CoreGroup> findFirstByCodeIgnoreCase(String code);
	
	List<CoreGroup> findByCodeInIgnoreCase(Set<String> codes);
	
	List<CoreGroup> findByStatusTrue();
	
	List<CoreGroup> findByIdIn(Set<Long> ids);
	
}
