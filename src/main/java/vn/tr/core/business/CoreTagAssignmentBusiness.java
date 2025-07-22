package vn.tr.core.business;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.core.dao.model.CoreTagAssignment;
import vn.tr.core.dao.service.CoreTagAssignmentService;
import vn.tr.core.dao.service.CoreTagService;
import vn.tr.core.data.dto.CoreTagAssignmentData;
import vn.tr.core.data.mapper.CoreTagAssignmentMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class CoreTagAssignmentBusiness {
	
	private final CoreTagAssignmentService coreTagAssignmentService;
	private final CoreTagService coreTagService; // Cần để đảm bảo tag tồn tại
	private final CoreTagAssignmentMapper coreTagAssignmentMapper;
	
	public CoreTagAssignmentData create(CoreTagAssignmentData assignmentData) {
		// 1. Đảm bảo tag được gán tồn tại (hoặc tự động tạo nó)
		coreTagService.findOrCreate(assignmentData.getTagCode());
		
		// 2. Chuyển đổi DTO sang Entity và lưu
		CoreTagAssignment assignment = coreTagAssignmentMapper.toEntity(assignmentData);
		CoreTagAssignment savedAssignment = coreTagAssignmentService.save(assignment);
		
		return coreTagAssignmentMapper.toData(savedAssignment);
	}
	
	public CoreTagAssignmentData update(Long id, CoreTagAssignmentData assignmentData) {
		CoreTagAssignment assignment = coreTagAssignmentService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreTagAssignment.class, id));
		
		// Cập nhật các trường từ DTO
		coreTagAssignmentMapper.updateEntityFromData(assignmentData, assignment);
		CoreTagAssignment updatedAssignment = coreTagAssignmentService.save(assignment);
		
		return coreTagAssignmentMapper.toData(updatedAssignment);
	}
	
	public void delete(Long id) {
		if (!coreTagAssignmentService.existsById(id)) {
			throw new EntityNotFoundException(CoreTagAssignment.class, id);
		}
		coreTagAssignmentService.deleteById(id);
	}
	
	@Transactional(readOnly = true)
	public CoreTagAssignmentData findById(Long id) {
		return coreTagAssignmentService.findById(id)
				.map(coreTagAssignmentMapper::toData)
				.orElseThrow(() -> new EntityNotFoundException(CoreTagAssignment.class, id));
	}
}
