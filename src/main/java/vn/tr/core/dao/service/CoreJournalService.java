package vn.tr.core.dao.service;

import vn.tr.core.dao.model.CoreJournal;

import java.util.List;
import java.util.Optional;

public interface CoreJournalService {

	void delete(Long id);

	boolean existsById(Long id);

	Optional<CoreJournal> findById(Long id);

	Optional<CoreJournal> findByIdAndDaXoaFalse(Long id);

	CoreJournal save(CoreJournal coreJournal);

	List<CoreJournal> findByObjectIdAndObjectTypeAndAppCodeAndDaXoaFalseOrderByNgayTaoDesc(Long objectId, String objectType, String appCode);

}
