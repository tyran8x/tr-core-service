package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.jpa.helper.AssociationSyncHelper;
import vn.tr.core.dao.model.CoreUserRole;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreUserRoleServiceImpl implements CoreUserRoleService {
	
	private final CoreUserRoleRepo coreUserRoleRepo;
	private final AssociationSyncHelper associationSyncHelper;
	
	@Override
	public void deleteById(Long id) {
		coreUserRoleRepo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return coreUserRoleRepo.existsById(id);
	}
	
	@Override
	public Optional<CoreUserRole> findById(Long id) {
		return coreUserRoleRepo.findById(id);
	}
	
	@Override
	public CoreUserRole save(CoreUserRole coreUserRole) {
		return coreUserRoleRepo.save(coreUserRole);
	}
	
	@Override
	@Transactional
	public void synchronizeUserRolesInApp(String username, String appCode, Set<String> newRoleCodes) {
		log.info("Bắt đầu đồng bộ hóa các vai trò cho người dùng '{}' trong ứng dụng '{}'", username, appCode);
		
		var ownerContext = new CoreUserRoleContext(username.toLowerCase(), appCode);
		
		List<CoreUserRole> existingAssignments = coreUserRoleRepo.findAllByUsernameAndAppCodeIncludingDeleted(
				ownerContext.username(), ownerContext.appCode());
		
		associationSyncHelper.synchronize(
				ownerContext,
				existingAssignments,
				newRoleCodes,
				CoreUserRole::getRoleCode,
				CoreUserRole::new,
				(association, context) -> {
					association.setUsername(context.username());
					association.setAppCode(context.appCode());
				},
				CoreUserRole::setRoleCode,
				coreUserRoleRepo);
	}
	
	@Override
	@Transactional
	public void assignRoleToUserInApp(String username, String appCode, String roleCode) {
		// Lấy danh sách các vai trò hiện tại của người dùng trong app đó
		Set<String> currentRoles = this.findActiveRoleCodesByUsernameAndAppCode(username, appCode);
		
		// Nếu vai trò chưa tồn tại, thêm nó vào và gọi lại hàm đồng bộ
		if (!currentRoles.contains(roleCode)) {
			currentRoles.add(roleCode);
			synchronizeUserRolesInApp(username, appCode, currentRoles);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public Set<String> findActiveRoleCodesByUsernameAndAppCode(String username, String appCode) {
		// Không cần kiểm tra null vì đã có @NonNull ở interface
		if (username.isBlank() || appCode.isBlank()) {
			return Collections.emptySet();
		}
		return coreUserRoleRepo.findActiveRoleCodesByUsernameAndAppCode(username.toLowerCase(), appCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Set<String> findAllActiveRoleCodesByUsername(String username) {
		if (username.isBlank()) {
			return Collections.emptySet();
		}
		return coreUserRoleRepo.findAllActiveRoleCodesByUsername(username.toLowerCase());
	}
	
	private record CoreUserRoleContext(String username, String appCode) {
	}
	
}
