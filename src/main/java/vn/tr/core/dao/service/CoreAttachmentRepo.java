package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreAttachment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoreAttachmentRepo extends JpaRepository<CoreAttachment, Long> {

	Optional<CoreAttachment> findByIdAndDaXoaFalse(Long id);

	List<CoreAttachment> findByIdInAndAppCodeAndDaXoaFalse(List<Long> idList, String appCode);

	List<CoreAttachment> findByIdInAndAppCodeAndObjectIdAndDaXoaFalse(List<Long> idList, String appCode, Long objectId);

	List<CoreAttachment> findByIdInAndDaXoaFalse(List<Long> idList);

	List<CoreAttachment> findByObjectIdAndAppCodeAndDaXoaFalse(Long objectId, String appCode);

	List<CoreAttachment> findByObjectIdAndAppCodeAndTypeAndDaXoaFalse(Long objectId, String appCode, Integer type);

	Optional<CoreAttachment> findFirstByCode(String code);

	@Modifying(clearAutomatically = true)
	@Query("update CoreAttachment u set u.daXoa = ?1 where u.objectId = ?2 and u.appCode = ?3")
	int setFixedDaXoaForObjectIdAndAppCode(boolean daXoa, Long objectId, String appCode);

	@Modifying(clearAutomatically = true)
	@Query("update CoreAttachment u set u.daXoa = ?1 where u.objectId = ?2 and u.appCode = ?3 and u.type = ?4")
	int setFixedDaXoaForObjectIdAndAppCodeAndType(boolean daXoa, Long objectId, String appCode, int type);
}
