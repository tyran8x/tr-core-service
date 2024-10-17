package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreJournalDetail;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoreJournalDetailRepo extends JpaRepository<CoreJournalDetail, Long>, JpaSpecificationExecutor<CoreJournalDetail> {

	List<CoreJournalDetail> findByDaXoaFalse();

	Optional<CoreJournalDetail> findByIdAndDaXoaFalse(Long id);

	List<CoreJournalDetail> findByJournalIdAndDaXoaFalse(Long journalId);

	@Modifying(clearAutomatically = true)
	@Query("update CoreJournalDetail u set u.daXoa = ?1 where u.journalId = ?2")
	int setFixedDaXoaForJournalId(boolean daXoa, Long journalId);

}
