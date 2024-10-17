package vn.tr.core.dao.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreJournal;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CoreJournalServiceImpl implements CoreJournalService {

	private final CoreJournalRepo repo;

	public CoreJournalServiceImpl(CoreJournalRepo repo) {
		this.repo = repo;
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
	public Optional<CoreJournal> findById(Long id) {
		return repo.findById(id);
	}

	@Override
	public Optional<CoreJournal> findByIdAndDaXoaFalse(Long id) {
		return repo.findByIdAndDaXoaFalse(id);
	}

	@Override
	public CoreJournal save(CoreJournal coreJournal) {
		return repo.save(coreJournal);
	}

	@Override
	public List<CoreJournal> findByObjectIdAndObjectTypeAndAppCodeAndDaXoaFalseOrderByNgayTaoDesc(Long objectId, String objectType, String appCode) {
		return repo.findByObjectIdAndObjectTypeAndAppCodeAndDaXoaFalseOrderByNgayTaoDesc(objectId, objectType, appCode);
	}

}
