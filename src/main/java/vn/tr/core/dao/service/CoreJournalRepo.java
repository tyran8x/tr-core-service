package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreJournal;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoreJournalRepo extends JpaRepository<CoreJournal, Long>, JpaSpecificationExecutor<CoreJournal> {

	List<CoreJournal> findByDaXoaFalse();

	Optional<CoreJournal> findByIdAndDaXoaFalse(Long id);

	List<CoreJournal> findByObjectIdAndObjectTypeAndAppCodeAndDaXoaFalseOrderByNgayTaoDesc(Long objectId, String objectType, String appCode);
}
