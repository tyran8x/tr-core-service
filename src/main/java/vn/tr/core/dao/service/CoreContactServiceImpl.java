package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreContact;
import vn.tr.core.data.dto.CoreContactData;
import vn.tr.core.data.mapper.CoreContactMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreContactServiceImpl implements CoreContactService {
	
	private final CoreContactRepo coreContactRepo;
	private final CoreContactMapper coreContactMapper;
	
	@Override
	public Optional<CoreContact> findById(Long id) {
		return coreContactRepo.findById(id);
	}
	
	@Override
	public CoreContact save(CoreContact coreContact) {
		return coreContactRepo.save(coreContact);
	}
	
	@Override
	public void delete(Long id) {
		coreContactRepo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return coreContactRepo.existsById(id);
	}
	
	@Override
	@Transactional
	public void synchronizeContactsForOwnerInApp(String ownerType, String ownerValue, String appCode,
			Collection<CoreContactData> contactDataList) {
		log.info("Bắt đầu đồng bộ hóa danh bạ cho owner='{}', value='{}', app='{}'", ownerType, ownerValue, appCode);
		
		List<CoreContact> existingContacts = coreContactRepo.findAllByOwnerInAppIncludingDeleted(ownerType, ownerValue, appCode);
		Map<Long, CoreContact> existingContactMap = existingContacts.stream()
				.collect(Collectors.toMap(CoreContact::getId, Function.identity()));
		
		List<CoreContact> contactsToSave = new ArrayList<>();
		List<CoreContact> finalActiveContacts = new ArrayList<>();
		
		// 2. Xử lý các DTO đầu vào (thêm mới hoặc cập nhật)
		for (CoreContactData dto : contactDataList) {
			CoreContact contact;
			if (dto.getId() != null && existingContactMap.containsKey(dto.getId())) {
				// CẬP NHẬT
				contact = existingContactMap.get(dto.getId());
				coreContactMapper.updateEntityFromData(dto, contact);
				if (contact.getDeletedAt() != null) {
					contact.setDeletedAt(null);
				}
				existingContactMap.remove(dto.getId());
			} else {
				// THÊM MỚI
				contact = coreContactMapper.toEntity(dto);
				contact.setOwnerType(ownerType);
				contact.setOwnerValue(ownerValue);
				contact.setAppCode(appCode);
			}
			contactsToSave.add(contact);
			finalActiveContacts.add(contact);
		}
		
		// 3. Xử lý các liên hệ cần XÓA MỀM
		for (CoreContact contactToDelete : existingContactMap.values()) {
			if (contactToDelete.getDeletedAt() == null) {
				contactToDelete.setDeletedAt(LocalDateTime.now());
				contactsToSave.add(contactToDelete);
			}
		}
		
		// 4. Lưu tất cả thay đổi vào DB
		if (!contactsToSave.isEmpty()) {
			coreContactRepo.saveAll(contactsToSave);
			log.info("Đã lưu {} thay đổi cho danh bạ của owner='{}', value='{}'", contactsToSave.size(), ownerType, ownerValue);
		}
		
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CoreContact> findActiveByOwnerInApp(String ownerType, String ownerValue, String appCode) {
		return coreContactRepo.findByOwnerTypeAndOwnerValueAndAppCode(ownerType, ownerValue, appCode);
	}
	
}
