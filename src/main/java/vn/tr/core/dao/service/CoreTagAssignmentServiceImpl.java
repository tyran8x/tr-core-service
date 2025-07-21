package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.tr.core.dao.model.CoreTagAssignment;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoreTagAssignmentServiceImpl implements CoreTagAssignmentService {
	
	private final CoreTagAssignmentRepo coreTagAssignmentRepo;
	
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
	public Optional<CoreTagAssignment> findFirstByTagIdAndTaggableValueAndTaggableType(Long tagId, String taggableValue, String taggableType) {
		return coreTagAssignmentRepo.findFirstByTagIdAndTaggableValueAndTaggableType(tagId, taggableValue, taggableType);
	}
	
}
