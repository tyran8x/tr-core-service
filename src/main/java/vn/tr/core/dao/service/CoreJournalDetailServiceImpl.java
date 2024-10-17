package vn.tr.core.dao.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreJournalDetail;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CoreJournalDetailServiceImpl implements CoreJournalDetailService {

	private final CoreJournalDetailRepo repo;

	public CoreJournalDetailServiceImpl(CoreJournalDetailRepo repo) {
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
	public Optional<CoreJournalDetail> findById(Long id) {
		return repo.findById(id);
	}

	@Override
	public Optional<CoreJournalDetail> findByIdAndDaXoaFalse(Long id) {
		return repo.findByIdAndDaXoaFalse(id);
	}

	@Override
	public CoreJournalDetail save(CoreJournalDetail coreJournalDetail) {
		return repo.save(coreJournalDetail);
	}

	@Override
	public List<CoreJournalDetail> findByJournalIdAndDaXoaFalse(Long journalId) {
		return repo.findByJournalIdAndDaXoaFalse(journalId);
	}

	@Override
	public int setFixedDaXoaForJournalId(boolean daXoa, Long journalId) {
		return repo.setFixedDaXoaForJournalId(daXoa, journalId);
	}

}
