package vn.tr.core.dao.service;

import vn.tr.core.dao.model.CoreAttachment;

import java.util.List;
import java.util.Optional;

public interface CoreAttachmentService {

	void delete(CoreAttachment coreAttachment);

	Optional<CoreAttachment> findById(Long id);

	Optional<CoreAttachment> findByIdAndDaXoaFalse(Long id);

	List<CoreAttachment> findByIdInAndAppCodeAndDaXoaFalse(List<Long> idList, String appCode);

	List<CoreAttachment> findByIdInAndAppCodeAndObjectIdAndDaXoaFalse(List<Long> idList, String appCode, Long objectId);

	List<CoreAttachment> findByIdInAndDaXoaFalse(List<Long> idList);

	List<CoreAttachment> findByObjectIdAndAppCodeAndDaXoaFalse(Long objectId, String appCode);

	List<CoreAttachment> findByObjectIdAndAppCodeAndTypeAndDaXoaFalse(Long objectId, String appCode, Integer type);

	Optional<CoreAttachment> findFirstByCode(String code);

	CoreAttachment save(CoreAttachment coreAttachment);

	CoreAttachment saveAndCopy(CoreAttachment oldCoreAttachment, CoreAttachment newCoreAttachment);

	CoreAttachment saveAndMove(CoreAttachment coreAttachment);

	int setFixedDaXoaForObjectIdAndAppCode(boolean daXoa, Long objectId, String appCode);

	int setFixedDaXoaForObjectIdAndAppCodeAndType(boolean daXoa, Long objectId, String appCode, int type);

}
