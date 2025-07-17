package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.tr.core.dao.model.CoreTagAssignment;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoreTagAssignmentServiceImpl implements CoreTagAssignmentService {
	
	private final CoreTagAssignmentRepo repo;
	
	@Override
	public void deleteById(Long id) {
		repo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}
	
	@Override
	public Optional<CoreTagAssignment> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public CoreTagAssignment save(CoreTagAssignment coreUserApp) {
		return repo.save(coreUserApp);
	}
	
	@Override
	public Optional<CoreTagAssignment> findFirstByTagIdAndTaggableValueAndTaggableType(Long tagId, String taggableValue, String taggableType) {
		return repo.findFirstByTagIdAndTaggableValueAndTaggableType(tagId, taggableValue, taggableType);
	}
	
}
