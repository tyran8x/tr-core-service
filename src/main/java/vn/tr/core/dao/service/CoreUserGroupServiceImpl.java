package vn.tr.core.dao.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreUserGroup;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CoreUserGroupServiceImpl implements CoreUserGroupService {
	
	private final CoreUserGroupRepo repo;
	
	public CoreUserGroupServiceImpl(CoreUserGroupRepo repo) {
		this.repo = repo;
	}
	
	@Override
	public CoreUserGroup save(CoreUserGroup coreUser2Group) {
		return repo.save(coreUser2Group);
	}
	
	@Override
	public void deleteById(Long id) {
		repo.deleteById(id);
	}
	
	@Override
	public Optional<CoreUserGroup> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public void replaceUserGroups(String username, Set<String> newGroupCodes) {
		
		List<CoreUserGroup> existingAssignments = repo.findByUsernameIgnoreCase(username);
		
		if (newGroupCodes.isEmpty() && existingAssignments.isEmpty()) {
			return;
		}
		
		Set<String> existingGroupCodes = existingAssignments.stream().map(CoreUserGroup::getGroupCode).collect(Collectors.toSet());
		
		if (existingGroupCodes.equals(newGroupCodes)) {
			return;
		}
		
		List<CoreUserGroup> toDelete = existingAssignments.stream().filter(ua -> !newGroupCodes.contains(ua.getGroupCode())).toList();
		if (!toDelete.isEmpty()) {
			repo.deleteAllInBatch(toDelete);
		}
		
		Set<String> toAdd = newGroupCodes.stream().filter(code -> !existingGroupCodes.contains(code)).collect(Collectors.toSet());
		
		if (!toAdd.isEmpty()) {
			List<CoreUserGroup> newAssignments = toAdd.stream().map(groupCode -> CoreUserGroup.builder()
					.username(username)
					.groupCode(groupCode)
					.build()).toList();
			repo.saveAll(newAssignments);
		}
	}
	
	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Set<String> findGroupCodesByUsername(String username) {
		if (username.isBlank()) {
			return Collections.emptySet();
		}
		return repo.findGroupCodesByUsername(username.toLowerCase());
	}
	
}
