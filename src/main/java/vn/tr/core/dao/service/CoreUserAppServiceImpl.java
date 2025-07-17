package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.core.dao.model.CoreUserApp;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoreUserAppServiceImpl implements CoreUserAppService {
	
	private final CoreUserAppRepo repo;
	
	@Override
	public void deleteById(Long id) {
		repo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}
	
	@Override
	public Optional<CoreUserApp> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public CoreUserApp save(CoreUserApp coreUserApp) {
		return repo.save(coreUserApp);
	}
	
	@Transactional
	public void replaceUserApps(String username, Set<String> newAppCodes) {
		List<CoreUserApp> allCurrentAssignments = repo.findAllByUsernameIncludingDeleted(username);
		Map<String, CoreUserApp> existingAssignmentsMap = allCurrentAssignments.stream()
				.collect(Collectors.toMap(CoreUserApp::getAppCode, Function.identity()));
		
		Set<String> currentlyActiveAppCodes = allCurrentAssignments.stream()
				.filter(a -> a.getDeletedAt() == null)
				.map(CoreUserApp::getAppCode)
				.collect(Collectors.toSet());
		if (currentlyActiveAppCodes.equals(newAppCodes)) {
			return;
		}
		
		List<CoreUserApp> toSaveOrUpdate = new ArrayList<>();
		List<CoreUserApp> toSoftDelete = new ArrayList<>();
		
		existingAssignmentsMap.forEach((existingAppCode, assignment) -> {
			boolean isInNewList = newAppCodes.contains(existingAppCode);
			boolean isCurrentlyDeleted = assignment.getDeletedAt() != null;
			
			if (isInNewList && isCurrentlyDeleted) {
				assignment.setDeletedAt(null);
				assignment.setStatus(LifecycleStatus.ACTIVE);
				assignment.setAssignedAt(LocalDateTime.now());
				toSaveOrUpdate.add(assignment);
			} else if (!isInNewList && !isCurrentlyDeleted) {
				toSoftDelete.add(assignment);
			}
		});
		
		newAppCodes.forEach(newAppCode -> {
			if (!existingAssignmentsMap.containsKey(newAppCode)) {
				CoreUserApp newAssignment = CoreUserApp.builder()
						.username(username)
						.appCode(newAppCode)
						.build();
				toSaveOrUpdate.add(newAssignment);
			}
		});
		
		if (!toSaveOrUpdate.isEmpty()) {
			repo.saveAll(toSaveOrUpdate);
		}
		if (!toSoftDelete.isEmpty()) {
			repo.deleteAll(toSoftDelete);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public Set<String> findAppCodesByUsername(String username) {
		if (username.isBlank()) {
			return Collections.emptySet();
		}
		return repo.findAppCodesByUsername(username.toLowerCase());
	}
	
	@Override
	public boolean existsByUsernameAndAppCode(String username, String appCode) {
		return repo.existsByUsernameAndAppCode(username, appCode);
	}
	
	@Override
	@Transactional
	public void assignUserToAppIfNotExists(String username, String appCode) {
		String normalizedUsername = username.toLowerCase();
		
		List<CoreUserApp> assignments = repo.findAllByUsernameIncludingDeleted(normalizedUsername);
		
		Optional<CoreUserApp> existingAssignment = assignments.stream()
				.filter(a -> a.getAppCode().equalsIgnoreCase(appCode))
				.findFirst();
		
		if (existingAssignment.isPresent()) {
			CoreUserApp assignment = existingAssignment.get();
			if (assignment.getDeletedAt() != null) {
				assignment.setDeletedAt(null);
				assignment.setStatus(LifecycleStatus.ACTIVE);
				repo.save(assignment);
			}
		} else {
			CoreUserApp newAssignment = CoreUserApp.builder()
					.username(normalizedUsername)
					.appCode(appCode)
					.build();
			repo.save(newAssignment);
		}
	}
	
}
