package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.SystemUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreAttachment;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CoreAttachmentServiceImpl implements CoreAttachmentService {
	private final CoreAttachmentRepo repo;
	@Value("${core.attachment.path.upload}")
	String coreAttachmentPathUpload;
	String left = "/";
	String right = "\\";

	@Override
	public void delete(CoreAttachment coreAttachment) {
		repo.delete(coreAttachment);
	}

	@Override
	public Optional<CoreAttachment> findById(Long id) {
		return repo.findById(id);
	}

	@Override
	public Optional<CoreAttachment> findByIdAndDaXoaFalse(Long id) {
		return repo.findByIdAndDaXoaFalse(id);
	}

	@Override
	public List<CoreAttachment> findByIdInAndAppCodeAndDaXoaFalse(List<Long> idList, String appCode) {
		return repo.findByIdInAndAppCodeAndDaXoaFalse(idList, appCode);
	}

	@Override
	public List<CoreAttachment> findByIdInAndAppCodeAndObjectIdAndDaXoaFalse(List<Long> idList, String appCode, Long objectId) {
		return repo.findByIdInAndAppCodeAndObjectIdAndDaXoaFalse(idList, appCode, objectId);
	}

	@Override
	public List<CoreAttachment> findByIdInAndDaXoaFalse(List<Long> idList) {
		return repo.findByIdInAndDaXoaFalse(idList);
	}

	@Override
	public List<CoreAttachment> findByObjectIdAndAppCodeAndDaXoaFalse(Long objectId, String appCode) {
		return repo.findByObjectIdAndAppCodeAndDaXoaFalse(objectId, appCode);
	}

	@Override
	public List<CoreAttachment> findByObjectIdAndAppCodeAndTypeAndDaXoaFalse(Long objectId, String appCode, Integer type) {
		return repo.findByObjectIdAndAppCodeAndTypeAndDaXoaFalse(objectId, appCode, type);
	}

	@Override
	public Optional<CoreAttachment> findFirstByCode(String code) {
		return repo.findFirstByCode(code);
	}

	@Override
	public CoreAttachment save(CoreAttachment coreAttachment) {
		return repo.save(coreAttachment);
	}

	@Override
	@Transactional
	public CoreAttachment saveAndCopy(CoreAttachment oldCoreAttachment, CoreAttachment newCoreAttachment) {
		try {
			boolean isOsWindows = SystemUtils.IS_OS_WINDOWS;
			Path pathOld;
			Path pathNew;
			Path folderNew;
			if (isOsWindows) {
				folderNew = Paths.get(coreAttachmentPathUpload + right + newCoreAttachment.getYear() + right + newCoreAttachment.getMonth());
				pathNew = Paths.get(coreAttachmentPathUpload + right + newCoreAttachment.getYear() + right + newCoreAttachment.getMonth() + right
						+ newCoreAttachment.getCode());
				pathOld = Paths.get(oldCoreAttachment.getFolder() + right + oldCoreAttachment.getCode());
			} else {
				folderNew = Paths.get(coreAttachmentPathUpload + left + newCoreAttachment.getYear() + left + newCoreAttachment.getMonth());
				pathNew = Paths.get(coreAttachmentPathUpload + left + newCoreAttachment.getYear() + left + newCoreAttachment.getMonth() + left
						+ newCoreAttachment.getCode());
				pathOld = Paths.get(oldCoreAttachment.getFolder() + left + oldCoreAttachment.getCode());
			}
			Files.createDirectories(folderNew);
			Files.copy(pathOld, pathNew, StandardCopyOption.COPY_ATTRIBUTES);
			newCoreAttachment.setFolder(folderNew.toString());
			newCoreAttachment = repo.save(newCoreAttachment);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return newCoreAttachment;
	}

	@Override
	@Transactional
	public CoreAttachment saveAndMove(CoreAttachment coreAttachment) {
		try {
			boolean isOsWindows = SystemUtils.IS_OS_WINDOWS;
			Path pathOld;
			Path pathNew;
			Path folderNew;
			if (isOsWindows) {
				folderNew = Paths.get(coreAttachmentPathUpload + right + coreAttachment.getYear() + right + coreAttachment.getMonth());
				pathNew = Paths.get(coreAttachmentPathUpload + right + coreAttachment.getYear() + right + coreAttachment.getMonth() + right
						+ coreAttachment.getCode());
				pathOld = Paths.get(coreAttachment.getFolder() + right + coreAttachment.getCode());
			} else {
				folderNew = Paths.get(coreAttachmentPathUpload + left + coreAttachment.getYear() + left + coreAttachment.getMonth());
				pathNew = Paths.get(coreAttachmentPathUpload + left + coreAttachment.getYear() + left + coreAttachment.getMonth() + left
						+ coreAttachment.getCode());
				pathOld = Paths.get(coreAttachment.getFolder() + left + coreAttachment.getCode());
			}
			Files.createDirectories(folderNew);
			Files.move(pathOld, pathNew, StandardCopyOption.REPLACE_EXISTING);
			coreAttachment.setFolder(folderNew.toString());
			coreAttachment = repo.save(coreAttachment);
		} catch (Exception e) {
			coreAttachment = null;
			log.error(e.getMessage());
		}
		return coreAttachment;
	}

	@Override
	public int setFixedDaXoaForObjectIdAndAppCode(boolean daXoa, Long objectId, String appCode) {
		return repo.setFixedDaXoaForObjectIdAndAppCode(daXoa, objectId, appCode);
	}

	@Override
	public int setFixedDaXoaForObjectIdAndAppCodeAndType(boolean daXoa, Long objectId, String appCode, int type) {
		return repo.setFixedDaXoaForObjectIdAndAppCodeAndType(daXoa, objectId, appCode, type);
	}

}
