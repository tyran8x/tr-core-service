package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.jpa.helper.AssociationSyncHelper;
import vn.tr.core.dao.model.CoreTag;
import vn.tr.core.dao.model.CoreTagAssignment;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CoreTagAssignmentServiceImpl implements CoreTagAssignmentService {
	
	private final CoreTagAssignmentRepo coreTagAssignmentRepo;
	private final CoreTagService coreTagService;
	private final AssociationSyncHelper associationSyncHelper;
	
	@Override
	public void deleteById(Long id) {
		coreTagAssignmentRepo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return coreTagAssignmentRepo.existsById(id);
	}
	
	@Override
	public Optional<CoreTagAssignment> findById(Long id) {
		return coreTagAssignmentRepo.findById(id);
	}
	
	@Override
	public CoreTagAssignment save(CoreTagAssignment coreUserApp) {
		return coreTagAssignmentRepo.save(coreUserApp);
	}
	
	@Override
	@Transactional
	public void synchronizeTagsForTaggable(String taggableType, String taggableValue, Set<String> newTagCodes) {
		
		record TaggableContext(String type, String value) {
		}
		var ownerContext = new TaggableContext(taggableType, taggableValue);
		
		// **SỬA LỖI TẠI ĐÂY**
		associationSyncHelper.synchronize(
				// Tham số 1: ownerContext - Ngữ cảnh của "chủ thể" được gán tag
				ownerContext,
				
				// Tham số 2: existingAssociations - Danh sách liên kết cũ
				coreTagAssignmentRepo.findAllByTaggableIncludingDeleted(taggableType, taggableValue),
				
				// Tham số 3: newKeys - Tập hợp các khóa mới (tag_code)
				newTagCodes,
				
				// Tham số 4: keyExtractor - Hàm để lấy khóa từ bản ghi liên kết cũ
				CoreTagAssignment::getTagCode,
				
				// Tham số 5: associationFactory - Hàm để tạo một bản ghi liên kết mới (rỗng)
				CoreTagAssignment::new,
				
				// Tham số 6: ownerContextSetter - Hàm để gán thông tin từ "chủ thể" vào bản ghi mới
				(assignment, context) -> {
					assignment.setTaggableType(context.type());
					assignment.setTaggableValue(context.value());
				},
				
				// Tham số 7: keySetter - Hàm để gán "khóa" (tag_code) vào bản ghi mới
				CoreTagAssignment::setTagCode,
				
				// Tham số 8: repository - Repository để helper tự lưu và xóa
				coreTagAssignmentRepo
		                                 );
	}
	
	/**
	 * Kiểm tra xem một CoreTag có đang được sử dụng hay không bằng cách dùng tag_code.
	 */
	@Override
	public boolean isTagInUse(CoreTag tag) {
		if (tag.getCode() == null) {
			return false;
		}
		return coreTagAssignmentRepo.existsByTagCode(tag.getCode());
	}
	
	@Override
	public JpaRepository<CoreTagAssignment, Long> getRepository() {
		return this.coreTagAssignmentRepo;
	}
	
}
