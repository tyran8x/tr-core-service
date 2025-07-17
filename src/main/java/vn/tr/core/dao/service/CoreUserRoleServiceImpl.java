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
	public void replaceUserRolesForApp(String username, String appCode, Set<String> newRoleCodesInApp) {
		List<CoreUserRole> allCurrentAssignmentsInApp = repo.findAllByUsernameAndAppCodeIncludingDeleted(username, appCode);
		
		Function<CoreUserRole, String> toRoleCode = CoreUserRole::getRoleCode;
		
		Set<String> currentlyActiveRoleCodes = allCurrentAssignmentsInApp.stream()
				.filter(a -> a.getDeletedAt() == null)
				.map(toRoleCode)
				.collect(Collectors.toSet());
		
		// Bước 3: Tối ưu hóa - Thoát sớm nếu không có gì thay đổi.
		if (currentlyActiveRoleCodes.equals(newRoleCodesInApp)) {
			return;
		}
		
		Map<String, CoreUserRole> existingMap = allCurrentAssignmentsInApp.stream()
				.collect(Collectors.toMap(CoreUserRole::getRoleCode, Function.identity()));
		
		List<CoreUserRole> toSaveOrUpdate = new ArrayList<>();
		List<CoreUserRole> toSoftDelete = new ArrayList<>();
		
		// Bước 5: Logic đồng bộ hóa.
		// Kích hoạt lại các vai trò đã bị xóa mềm.
		newRoleCodesInApp.stream()
				.filter(existingMap::containsKey) // Chỉ xét những role đã có trong DB
				.map(existingMap::get)
				.filter(assignment -> assignment.getDeletedAt() != null) // Lọc ra những cái đang bị xóa mềm
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
	
}
