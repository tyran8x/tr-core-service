package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.core.dao.model.CoreContact;
import vn.tr.core.data.criteria.CoreContactSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CoreContactServiceImpl implements CoreContactService {
	
	private final CoreContactRepo repo;
	
	public CoreContactServiceImpl(CoreContactRepo repo) {
		this.repo = repo;
	}
	
	@Override
	public Optional<CoreContact> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public CoreContact save(CoreContact coreContact) {
		return repo.save(coreContact);
	}
	
	@Override
	public void delete(Long id) {
		repo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}
	
	@Override
	public Page<CoreContact> findAll(CoreContactSearchCriteria coreContactSearchCriteria, Pageable pageable) {
		return repo.findAll(CoreContactSpecifications.quickSearch(coreContactSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreContact> findAll(CoreContactSearchCriteria coreContactSearchCriteria) {
		return repo.findAll(CoreContactSpecifications.quickSearch(coreContactSearchCriteria));
	}
	
	@Override
	public boolean existsById(long id) {
		return repo.existsById(id);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Set<Long> ids) {
		if (ids.isEmpty()) {
			return;
		}
		repo.softDeleteByIds(ids);
	}
	
	@Override
	public List<CoreContact> findAllByOwnerIncludingDeleted(String ownerType, String ownerValue) {
		return repo.findAllByOwnerIncludingDeleted(ownerType, ownerValue);
	}
	
	@Override
	public List<CoreContact> findActiveByOwner(String ownerType, String ownerValue) {
		return repo.findByOwnerTypeAndOwnerValueAndStatus(ownerType, ownerValue, LifecycleStatus.ACTIVE);
	}
	
	@Override
	public Optional<CoreContact> findPrimaryEmailByOwner(String ownerType, String ownerValue) {
		return repo.findPrimaryEmailByOwner(ownerType, ownerValue);
	}
	
	@Override
	public void saveAll(Iterable<CoreContact> coreContacts) {
		repo.saveAll(coreContacts);
	}
	
	@Override
	public void deleteByOwner(String ownerType, String ownerValue) {
		List<CoreContact> toDelete = repo.findByOwnerTypeAndOwnerValueAndStatus(ownerType, ownerValue, LifecycleStatus.ACTIVE);
		if (!toDelete.isEmpty()) {
			repo.deleteAll(toDelete);
		}
	}
	
}
