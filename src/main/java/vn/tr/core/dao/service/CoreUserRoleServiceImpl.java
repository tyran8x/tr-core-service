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
	public CoreUserRole save(CoreUserRole coreUserRole) {
		return repo.save(coreUserRole);
	}
	
	@Override
	public void deleteById(Long id) {
		repo.deleteById(id);
	}
	
	@Override
	public Optional<CoreUserRole> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}
	
	@Override
	@Transactional
	public void replaceUserRoles(String username, Map<String, Set<String>> newRoleAssignments) {
		
		// Bước 1: Lấy tất cả các bản ghi gán vai trò hiện có của user, kể cả đã bị xóa mềm.
		List<CoreUserRole> allCurrentAssignments = repo.findAllByUsernameIncludingDeleted(username);
		
		// Tạo một "khóa phức hợp" (composite key) dạng "appCode:roleCode" để dễ dàng tra cứu và so sánh
		Function<CoreUserRole, String> toCompositeKey = r -> r.getAppCode() + ":" + r.getRoleCode();
		
		Map<String, CoreUserRole> existingAssignmentsMap = allCurrentAssignments.stream()
				.collect(Collectors.toMap(toCompositeKey, Function.identity()));
		
		Set<String> newCompositeKeys = newRoleAssignments.entrySet().stream()
				.flatMap(entry -> entry.getValue().stream().map(roleCode -> entry.getKey() + ":" + roleCode))
				.collect(Collectors.toSet());
		
		// --- Tối ưu hóa: Thoát sớm nếu không có gì thay đổi ---
		Set<String> currentlyActiveCompositeKeys = allCurrentAssignments.stream()
				.filter(a -> a.getDeletedAt() == null)
				.map(toCompositeKey)
				.collect(Collectors.toSet());
		
		if (currentlyActiveCompositeKeys.equals(newCompositeKeys)) {
			return;
		}
		
		List<CoreUserRole> toSaveOrUpdate = new ArrayList<>();
		List<CoreUserRole> toSoftDelete = new ArrayList<>();
		
		existingAssignmentsMap.forEach((compositeKey, assignment) -> {
			boolean isInNewList = newCompositeKeys.contains(compositeKey);
			boolean isCurrentlyDeleted = assignment.getDeletedAt() != null;
			
			if (isInNewList && isCurrentlyDeleted) {
				assignment.setDeletedAt(null);
				toSaveOrUpdate.add(assignment);
			} else if (!isInNewList && !isCurrentlyDeleted) {
				toSoftDelete.add(assignment);
			}
		});
		
		newCompositeKeys.forEach(compositeKey -> {
			if (!existingAssignmentsMap.containsKey(compositeKey)) {
				String[] parts = compositeKey.split(":", 2);
				String appCode = parts[0];
				String roleCode = parts[1];
				
				CoreUserRole newAssignment = CoreUserRole.builder()
						.username(username)
						.appCode(appCode)
						.roleCode(roleCode)
						.build();
				toSaveOrUpdate.add(newAssignment);
			}
		});
		
		// Bước 4: Thực thi các thay đổi trên CSDL
		if (!toSaveOrUpdate.isEmpty()) {
			repo.saveAll(toSaveOrUpdate);
		}
		if (!toSoftDelete.isEmpty()) {
			repo.deleteAll(toSoftDelete);
		}
	}
	
}
