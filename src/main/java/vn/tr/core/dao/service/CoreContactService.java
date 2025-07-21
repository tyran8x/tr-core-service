package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tr.core.dao.model.CoreContact;
import vn.tr.core.data.criteria.CoreContactSearchCriteria;
import vn.tr.core.data.dto.CoreContactData;

import java.util.List;
import java.util.Optional;

public interface CoreContactService {
	
	Optional<CoreContact> findById(Long id);
	
	CoreContact save(CoreContact coreContact);
	
	void delete(Long id);
	
	boolean existsById(Long id);
	
	Page<CoreContact> findAll(CoreContactSearchCriteria coreContactSearchCriteria, Pageable pageable);
	
	List<CoreContact> findAll(CoreContactSearchCriteria coreContactSearchCriteria);
	
	boolean existsById(long id);
	
	void saveAll(Iterable<CoreContact> coreContacts);
	
	void synchronizeContactsForOwnerInApp(String ownerType, String ownerValue, String appCode, String primaryEmail,
			List<CoreContactData> newContactDtos);
	
	List<CoreContact> findActiveByOwnerInApp(String ownerType, String ownerValue, String appCode);
	
	List<CoreContact> findAllActiveByOwner(String ownerType, String ownerValue);
	
}
