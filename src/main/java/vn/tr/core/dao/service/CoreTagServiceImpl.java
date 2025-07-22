package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.jpa.helper.GenericUpsertHelper;
import vn.tr.core.dao.model.CoreTag;
import vn.tr.core.dao.model.CoreTagAssignment;
import vn.tr.core.data.criteria.CoreTagSearchCriteria;
import vn.tr.core.data.dto.CoreTagAssignmentData;
import vn.tr.core.data.dto.CoreTagData;
import vn.tr.core.data.mapper.CoreTagMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreTagServiceImpl implements CoreTagService {
	
	private final CoreTagRepo coreTagRepo;
	private final CoreTagAssignmentRepo coreTagAssignmentRepo;
	private final GenericUpsertHelper genericUpsertHelper;
	private final CoreTagMapper coreTagMapper;
	
	@Override
	public Optional<CoreTag> findById(Long id) {
		return coreTagRepo.findById(id);
	}
	
	@Override
	public CoreTag save(CoreTag coreTag) {
		return coreTagRepo.save(coreTag);
	}
	
	@Override
	public void delete(Long id) {
		coreTagRepo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return coreTagRepo.existsById(id);
	}
	
	@Override
	public Page<CoreTag> findAll(CoreTagSearchCriteria coreTagSearchCriteria, Pageable pageable) {
		return coreTagRepo.findAll(CoreTagSpecifications.quickSearch(coreTagSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreTag> findAll(CoreTagSearchCriteria coreTagSearchCriteria) {
		return coreTagRepo.findAll(CoreTagSpecifications.quickSearch(coreTagSearchCriteria));
	}
	
	@Override
	public boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode) {
		return coreTagRepo.existsByIdNotAndCodeIgnoreCaseAndAppCode(id, code, appCode);
	}
	
	@Override
	public boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode) {
		return coreTagRepo.existsByIdNotAndNameIgnoreCaseAndAppCode(id, name, appCode);
	}
	
	@Override
	public boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode) {
		return coreTagRepo.existsByCodeIgnoreCaseAndAppCode(code, appCode);
	}
	
	@Override
	public boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode) {
		return coreTagRepo.existsByNameIgnoreCaseAndAppCode(name, appCode);
	}
	
	@Override
	public boolean existsByIdAndAppCode(long id, String appCode) {
		return false;
	}
	
	@Override
	public boolean existsById(long id) {
		return coreTagRepo.existsById(id);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Set<Long> ids) {
		if (ids.isEmpty()) {
			return;
		}
		coreTagRepo.softDeleteByIds(ids);
	}
	
	@Override
	@Transactional
	public void assignTag(String taggableType, String taggableValue, String tagCode) {
		// 1. Đảm bảo tag này tồn tại trong bảng CoreTag
		ensureTagsExist(Set.of(tagCode));
		
		// 2. Tìm kiếm liên kết hiện có (kể cả đã xóa mềm), nhận về List
		List<CoreTagAssignment> existingAssignments = coreTagAssignmentRepo
				.findAllByTaggableAndTagCodeIncludingDeleted(taggableType, taggableValue, tagCode);
		
		if (existingAssignments.isEmpty()) {
			// Kịch bản 1: Chưa có liên kết nào -> Tạo mới hoàn toàn
			log.info("Gán mới tag '{}' cho đối tượng '{}:{}'", tagCode, taggableType, taggableValue);
			CoreTagAssignment newAssignment = CoreTagAssignment.builder()
					.taggableType(taggableType)
					.taggableValue(taggableValue)
					.tagCode(tagCode)
					.build();
			coreTagAssignmentRepo.save(newAssignment);
			return; // Kết thúc sớm
		}
		
		// Nếu chạy đến đây, có nghĩa là đã có ít nhất 1 bản ghi.
		if (existingAssignments.size() > 1) {
			log.warn("DỮ LIỆU TRÙNG LẶP! Tìm thấy {} liên kết cho tag '{}' và đối tượng '{}:{}'. " +
					"Sẽ chỉ xử lý bản ghi đầu tiên.", existingAssignments.size(), tagCode, taggableType, taggableValue);
		}
		
		// Kịch bản 2: Đã có liên kết -> Lấy bản ghi đầu tiên để xử lý
		CoreTagAssignment firstAssignment = existingAssignments.getFirst();
		
		// Chỉ thực hiện save nếu bản ghi đang bị xóa mềm
		if (firstAssignment.getDeletedAt() != null) {
			log.info("Kích hoạt lại tag '{}' cho đối tượng '{}:{}'", tagCode, taggableType, taggableValue);
			firstAssignment.setDeletedAt(null);
			coreTagAssignmentRepo.save(firstAssignment);
		}
		// Nếu không (deletedAt == null), không cần làm gì cả vì liên kết đã tồn tại và đang hoạt động.
	}
	
	private void ensureTagsExist(Set<String> tagCodes) {
		if (tagCodes.isEmpty()) {
			return;
		}
		
		// Tìm các tag đã tồn tại trong DB
		Set<String> existingCodes = coreTagRepo.findByCodeIn(tagCodes)
				.stream()
				.map(CoreTag::getCode)
				.collect(Collectors.toSet());
		
		// Tìm các tag chưa tồn tại
		List<CoreTag> newTags = tagCodes.stream()
				.filter(code -> !existingCodes.contains(code))
				.map(code -> CoreTag.builder().code(code).name(code).build()) // Tạo tag mới với name=code
				.collect(Collectors.toList());
		
		// Lưu các tag mới vào DB
		if (!newTags.isEmpty()) {
			log.info("Tạo mới {} tags: {}", newTags.size(), newTags.stream().map(CoreTag::getCode).collect(Collectors.toList()));
			coreTagRepo.saveAll(newTags);
		}
	}
	
	@Override
	@Transactional
	public void synchronizeTagsForTaggable(String taggableType, String taggableValue, List<CoreTagAssignmentData> newAssignmentDtos) {
		log.info("Bắt đầu đồng bộ hóa tags cho đối tượng '{}:{}'", taggableType, taggableValue);
		
		// 1. Đảm bảo tất cả các tag trong danh sách mới đều tồn tại
		Set<String> newTagCodes = newAssignmentDtos.stream().map(CoreTagAssignmentData::getTagCode).collect(Collectors.toSet());
		
		ensureTagsExist(newTagCodes);
		
		// 2. Lấy tất cả các liên kết cũ (kể cả đã xóa mềm) để so sánh
		List<CoreTagAssignment> oldAssignments = coreTagAssignmentRepo
				.findAllByTaggableIncludingDeleted(taggableType, taggableValue);
		
		Map<String, CoreTagAssignment> oldAssignmentsMapByTagCode = oldAssignments.stream()
				.collect(Collectors.toMap(CoreTagAssignment::getTagCode, Function.identity(), (a1, a2) -> a1));
		
		List<CoreTagAssignment> assignmentsToSave = new ArrayList<>();
		Set<String> processedTagCodes = new HashSet<>();
		
		// 3. Xử lý DTO mới: cập nhật liên kết đã có hoặc tạo mới
		for (CoreTagAssignmentData dto : newAssignmentDtos) {
			String tagCode = dto.getTagCode();
			CoreTagAssignment assignment;
			
			// Kịch bản A: Cập nhật liên kết đã có
			if (oldAssignmentsMapByTagCode.containsKey(tagCode)) {
				assignment = oldAssignmentsMapByTagCode.get(tagCode);
				if (assignment.getDeletedAt() != null) {
					assignment.setDeletedAt(null); // Kích hoạt lại
				}
			}
			// Kịch bản B: Tạo mới liên kết
			else {
				assignment = new CoreTagAssignment();
				assignment.setTaggableType(taggableType);
				assignment.setTaggableValue(taggableValue);
				assignment.setTagCode(tagCode);
			}
			// Cập nhật sortOrder (và các trường khác nếu DTO có)
			assignment.setSortOrder(dto.getSortOrder());
			assignmentsToSave.add(assignment);
			processedTagCodes.add(tagCode);
		}
		
		// 4. Xóa mềm các liên kết cũ không còn trong danh sách mới
		oldAssignments.stream()
				.filter(a -> a.getDeletedAt() == null && !processedTagCodes.contains(a.getTagCode()))
				.peek(a -> a.setDeletedAt(LocalDateTime.now()))
				.forEach(assignmentsToSave::add);
		
		// 5. Lưu tất cả thay đổi
		if (!assignmentsToSave.isEmpty()) {
			coreTagAssignmentRepo.saveAll(assignmentsToSave);
		}
	}
	
	@Override
	@Transactional
	public CoreTag upsert(CoreTagData coreTagData) {
		log.info("Thực hiện Upsert cho tag với code: {}", coreTagData.getCode());
		
		return genericUpsertHelper.upsert(
				coreTagData,
				() -> {
					List<CoreTag> results = coreTagRepo.findAllByCodeEvenIfDeleted(coreTagData.getCode());
					if (results.isEmpty()) return Optional.empty();
					if (results.size() > 1) {
						log.warn("Dữ liệu trùng lặp! Tìm thấy {} tag với cùng code '{}'.",
								results.size(), coreTagData.getCode());
					}
					return Optional.of(results.getFirst());
				},
				CoreTag::new,
				coreTagMapper::updateEntityFromData,
				coreTagRepo);
	}
	
}
