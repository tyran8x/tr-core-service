package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreTag;
import vn.tr.core.dao.model.CoreTagAssignment;
import vn.tr.core.data.criteria.CoreTagSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CoreTagServiceImpl implements CoreTagService {
	
	private final CoreTagRepo repo;
	private final CoreTagAssignmentRepo coreTagAssignmentRepo;
	
	@Override
	public Optional<CoreTag> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public CoreTag save(CoreTag coreTag) {
		return repo.save(coreTag);
	}
	
	@Override
	public void delete(Long id) {
		repo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}
	
	@Override
	public Page<CoreTag> findAll(CoreTagSearchCriteria coreTagSearchCriteria, Pageable pageable) {
		return repo.findAll(CoreTagSpecifications.quickSearch(coreTagSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreTag> findAll(CoreTagSearchCriteria coreTagSearchCriteria) {
		return repo.findAll(CoreTagSpecifications.quickSearch(coreTagSearchCriteria));
	}
	
	@Override
	public boolean existsByIdNotAndCodeIgnoreCase(long id, String code) {
		return repo.existsByIdNotAndCodeIgnoreCase(id, code);
	}
	
	@Override
	public boolean existsByIdNotAndNameIgnoreCase(long id, String name) {
		return repo.existsByIdNotAndNameIgnoreCase(id, name);
	}
	
	@Override
	public boolean existsByCodeIgnoreCase(String code) {
		return repo.existsByCodeIgnoreCase(code);
	}
	
	@Override
	public boolean existsByNameIgnoreCase(String name) {
		return repo.existsByNameIgnoreCase(name);
	}
	
	@Override
	public boolean existsById(long id) {
		return repo.existsById(id);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Set<Long> ids) {
		if (ids.isEmpty()) {
			return;
		}
		repo.softDeleteByIds(ids);
	}
	
	@Override
	@Transactional
	public void assignTag(String taggableValue, String taggableType, String tagCode) {
		CoreTag coreTag = repo.findFirstByCodeIgnoreCase(tagCode)
				.orElseGet(() -> {
					CoreTag newTag = new CoreTag();
					newTag.setCode(tagCode);
					newTag.setName(tagCode);
					return repo.save(newTag);
				});
		
		Optional<CoreTagAssignment> existingAssignment = coreTagAssignmentRepo.findFirstByTagIdAndTaggableValueAndTaggableType(coreTag.getId(),
				taggableValue, taggableType);
		
		if (existingAssignment.isEmpty()) {
			CoreTagAssignment newAssignment = new CoreTagAssignment();
			newAssignment.setTagId(coreTag.getId());
			newAssignment.setTaggableValue(taggableValue);
			newAssignment.setTaggableType(taggableType);
			coreTagAssignmentRepo.save(newAssignment);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean hasTag(String taggableValue, String taggableType, String tagCode) {
		return coreTagAssignmentRepo.existsByCoreTagCodeAndTaggableValueAndTaggableType(tagCode, taggableValue, taggableType);
	}
	
}
