package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreContact;
import vn.tr.core.data.criteria.CoreContactSearchCriteria;
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
	public Page<CoreContact> findAll(CoreContactSearchCriteria coreContactSearchCriteria, Pageable pageable) {
		return coreContactRepo.findAll(CoreContactSpecifications.quickSearch(coreContactSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreContact> findAll(CoreContactSearchCriteria coreContactSearchCriteria) {
		return coreContactRepo.findAll(CoreContactSpecifications.quickSearch(coreContactSearchCriteria));
	}
	
	@Override
	public boolean existsById(long id) {
		return coreContactRepo.existsById(id);
	}
	
	@Override
	public void saveAll(Iterable<CoreContact> coreContacts) {
		coreContactRepo.saveAll(coreContacts);
	}
	
	@Override
	@Transactional
	public void synchronizeContactsForOwnerInApp(String ownerType, String ownerValue, String appCode, String primaryEmail,
			List<CoreContactData> newContactDtos) {
		log.info("Bắt đầu đồng bộ hóa contact cho owner '{}' trong app '{}'", ownerValue, appCode);
		
		// 1. Đảm bảo email chính tồn tại và được cập nhật trong app này
		ensurePrimaryEmailExistsInApp(ownerType, ownerValue, appCode, primaryEmail);
		
		// 2. Lấy tất cả các contact cũ (kể cả đã xóa) trong app này để so sánh, TRỪ contact chính
		List<CoreContact> oldNonPrimaryContacts = coreContactRepo
				.findAllByOwnerInAppIncludingDeleted(ownerType, ownerValue, appCode)
				.stream()
				.filter(c -> !Boolean.TRUE.equals(c.getIsPrimary()))
				.toList();
		
		Map<Long, CoreContact> oldContactsMapById = oldNonPrimaryContacts.stream()
				.collect(Collectors.toMap(CoreContact::getId, Function.identity()));
		
		List<CoreContactData> newNonPrimaryDtos = newContactDtos.stream()
				.filter(dto -> !Boolean.TRUE.equals(dto.getIsPrimary()))
				.toList();
		
		List<CoreContact> contactsToSave = new ArrayList<>();
		Set<Long> processedIds = new HashSet<>();
		
		// 3. Xử lý các DTO mới: cập nhật contact đã có hoặc tạo mới
		for (CoreContactData dto : newNonPrimaryDtos) {
			CoreContact contact;
			// Kịch bản A: Cập nhật contact đã có
			if (dto.getId() != null && oldContactsMapById.containsKey(dto.getId())) {
				contact = oldContactsMapById.get(dto.getId());
				if (contact.getDeletedAt() != null) {
					contact.setDeletedAt(null); // Kích hoạt lại
				}
				processedIds.add(dto.getId());
			}
			// Kịch bản B: Tạo mới contact
			else {
				contact = new CoreContact();
				contact.setOwnerType(ownerType);
				contact.setOwnerValue(ownerValue);
				contact.setAppCode(appCode); // Gán appCode
			}
			// Áp dụng các thay đổi từ DTO và thêm vào danh sách lưu
			coreContactMapper.updateEntityFromData(dto, contact);
			contactsToSave.add(contact);
		}
		
		// 4. Xóa mềm các contact cũ không còn trong danh sách mới
		oldNonPrimaryContacts.stream()
				.filter(c -> c.getDeletedAt() == null && !processedIds.contains(c.getId()))
				.peek(c -> c.setDeletedAt(LocalDateTime.now()))
				.forEach(contactsToSave::add);
		
		// 5. Lưu tất cả các thay đổi vào DB trong một lần
		if (!contactsToSave.isEmpty()) {
			log.info("Lưu {} thay đổi về contact cho owner '{}' trong app '{}'", contactsToSave.size(), ownerValue, appCode);
			coreContactRepo.saveAll(contactsToSave);
		}
	}
	
	private void ensurePrimaryEmailExistsInApp(String ownerType, String ownerValue, String appCode, String primaryEmail) {
		if (primaryEmail.isBlank()) return;
		
		// Gọi phương thức repo mới trả về List
		List<CoreContact> primaryContacts = coreContactRepo
				.findByOwnerTypeAndOwnerValueAndAppCodeAndIsPrimaryTrue(ownerType, ownerValue, appCode);
		
		CoreContact primaryContactToSave = null;
		
		if (primaryContacts.isEmpty()) {
			// Kịch bản 1: Chưa có contact chính -> Tạo mới
			log.info("Tạo mới contact chính cho owner '{}' trong app '{}'", ownerValue, appCode);
			primaryContactToSave = CoreContact.builder()
					.ownerType(ownerType)
					.ownerValue(ownerValue)
					.appCode(appCode)
					.contactType("EMAIL")
					.value(primaryEmail)
					.isPrimary(true)
					.build();
		} else {
			if (primaryContacts.size() > 1) {
				// Kịch bản 2: Dữ liệu bị trùng! Đây là một sự cố cần ghi log.
				log.warn("DỮ LIỆU TRÙNG LẶP! Tìm thấy {} contact chính cho owner '{}' trong app '{}'. " +
						"Sẽ chỉ sử dụng contact đầu tiên.", primaryContacts.size(), ownerValue, appCode);
				// Bạn có thể thêm logic để thông báo cho hệ thống giám sát ở đây (ví dụ: qua Prometheus, Sentry...)
			}
			
			// Lấy contact đầu tiên trong danh sách để làm việc
			CoreContact firstPrimaryContact = primaryContacts.getFirst();
			
			// Kịch bản 3: Email chính đã thay đổi -> Cập nhật
			if (!primaryEmail.equalsIgnoreCase(firstPrimaryContact.getValue())) {
				log.info("Cập nhật contact chính cho owner '{}' trong app '{}'", ownerValue, appCode);
				firstPrimaryContact.setValue(primaryEmail);
				primaryContactToSave = firstPrimaryContact;
			}
		}
		
		// Chỉ gọi save nếu có sự thay đổi hoặc tạo mới
		if (primaryContactToSave != null) {
			coreContactRepo.save(primaryContactToSave);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CoreContact> findActiveByOwnerInApp(String ownerType, String ownerValue, String appCode) {
		return coreContactRepo.findByOwnerTypeAndOwnerValueAndAppCode(ownerType, ownerValue, appCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CoreContact> findAllActiveByOwner(String ownerType, String ownerValue) {
		return coreContactRepo.findByOwnerTypeAndOwnerValue(ownerType, ownerValue);
	}
	
}
