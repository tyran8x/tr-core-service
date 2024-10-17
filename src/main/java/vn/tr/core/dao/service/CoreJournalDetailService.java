package vn.tr.core.dao.service;

import vn.tr.core.dao.model.CoreJournalDetail;

import java.util.List;
import java.util.Optional;

public interface CoreJournalDetailService {

	void delete(Long id);

	boolean existsById(Long id);

	Optional<CoreJournalDetail> findById(Long id);

	Optional<CoreJournalDetail> findByIdAndDaXoaFalse(Long id);

	CoreJournalDetail save(CoreJournalDetail coreJournalDetail);

	List<CoreJournalDetail> findByJournalIdAndDaXoaFalse(Long journalId);

	int setFixedDaXoaForJournalId(boolean daXoa, Long journalId);

}
