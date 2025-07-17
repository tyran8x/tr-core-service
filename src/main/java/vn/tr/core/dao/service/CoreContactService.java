package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tr.core.dao.model.CoreContact;
import vn.tr.core.data.criteria.CoreContactSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CoreContactService {
	
	Optional<CoreContact> findById(Long id);
	
	CoreContact save(CoreContact coreContact);
	
	void delete(Long id);
	
	boolean existsById(Long id);
	
	Page<CoreContact> findAll(CoreContactSearchCriteria coreContactSearchCriteria, Pageable pageable);
	
	List<CoreContact> findAll(CoreContactSearchCriteria coreContactSearchCriteria);
	
	boolean existsById(long id);
	
	void deleteByIds(Set<Long> ids);
	
	List<CoreContact> findAllByOwnerIncludingDeleted(String ownerType, String ownerValue);
	
	List<CoreContact> findActiveByOwner(String ownerType, String ownerValue);
	
	Optional<CoreContact> findPrimaryEmailByOwner(String ownerType, String ownerValue);
	
	void saveAll(Iterable<CoreContact> coreContacts);
	
	void deleteByOwner(String ownerType, String ownerValue);
	
}
