package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreUserRole;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoreUserRoleServiceImpl implements CoreUserRoleService {
	
	private final CoreUserRoleRepo repo;
	
	@Override
	public void deleteById(Long id) {
		repo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}
	
	@Override
	public Optional<CoreUserRole> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public CoreUserRole save(CoreUserRole coreUserRole) {
		return repo.save(coreUserRole);
	}
	
	@Override
	@Transactional
	public void replaceUserRolesForApp(String username, String appCode, Set<String> newRoleCodesInApp) {
		List<CoreUserRole> allCurrentAssignmentsInApp = repo.findAllByUsernameAndAppCodeIncludingDeleted(username, appCode);
		
		Function<CoreUserRole, String> toRoleCode = CoreUserRole::getRoleCode;
		
		Set<String> currentlyActiveRoleCodes = allCurrentAssignmentsInApp.stream()
				.filter(a -> a.getDeletedAt() == null)
				.map(toRoleCode)
				.collect(Collectors.toSet());
		
		if (currentlyActiveRoleCodes.equals(newRoleCodesInApp)) {
			return;
		}
		
		Map<String, CoreUserRole> existingMap = allCurrentAssignmentsInApp.stream()
				.collect(Collectors.toMap(CoreUserRole::getRoleCode, Function.identity()));
		
		List<CoreUserRole> toSaveOrUpdate = new ArrayList<>();
		List<CoreUserRole> toSoftDelete = new ArrayList<>();
		
		newRoleCodesInApp.stream()
				.filter(existingMap::containsKey)
				.map(existingMap::get)
				.filter(assignment -> assignment.getDeletedAt() != null)
				.forEach(assignment -> {
					assignment.setDeletedAt(null);
					toSaveOrUpdate.add(assignment);
				});
		
		// Thêm các vai trò hoàn toàn mới.
		newRoleCodesInApp.stream()
				.filter(newRoleCode -> !existingMap.containsKey(newRoleCode))
				.map(newRoleCode -> CoreUserRole.builder()
						.username(username)
						.appCode(appCode)
						.roleCode(newRoleCode)
						.build())
				.forEach(toSaveOrUpdate::add);
		
		// Tìm các vai trò cần xóa mềm.
		allCurrentAssignmentsInApp.stream()
				.filter(assignment -> assignment.getDeletedAt() == null && !newRoleCodesInApp.contains(assignment.getRoleCode()))
				.forEach(toSoftDelete::add);
		
		// Bước 6: Thực thi các thay đổi trên CSDL.
		if (!toSaveOrUpdate.isEmpty()) {
			repo.saveAll(toSaveOrUpdate);
		}
		if (!toSoftDelete.isEmpty()) {
			repo.deleteAll(toSoftDelete);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public Set<String> findRoleCodesByUsername(String username) {
		if (username.isBlank()) {
			return Collections.emptySet();
		}
		return repo.findRoleCodesByUsername(username.toLowerCase());
	}
	
	@Override
	@Transactional
	public void assignRoleToUserIfNotExists(String username, String appCode, String roleCode) {
		String normalizedUsername = username.toLowerCase();
		
		Optional<CoreUserRole> existingAssignment = repo.findFirstByUsernameAndAppCodeAndRoleCode(normalizedUsername, appCode, roleCode);
		
		if (existingAssignment.isPresent()) {
			CoreUserRole assignment = existingAssignment.get();
			if (assignment.getDeletedAt() != null) {
				assignment.setDeletedAt(null);
				repo.save(assignment);
			}
		} else {
			CoreUserRole newAssignment = CoreUserRole.builder()
					.username(normalizedUsername)
					.appCode(appCode)
					.roleCode(roleCode)
					.build();
			repo.save(newAssignment);
		}
	}
	
}
