package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vn.tr.core.dao.model.CoreRole;
import vn.tr.core.dao.model.CoreUserRole;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Lớp triển khai cho CoreUserRoleService.
 * Chỉ chứa các logic truy vấn và CRUD cơ bản.
 *
 * @author tyran8x
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CoreUserRoleServiceImpl implements CoreUserRoleService {
	
	private final CoreUserRoleRepo coreUserRoleRepo;
	
	@Override
	public List<CoreUserRole> findByUsernameAndAppCodeIncludingDeleted(String username, String appCode) {
		return coreUserRoleRepo.findAllByUsernameAndAppCodeIncludingDeleted(username, appCode);
	}
	
	@Override
	public CoreUserRole save(CoreUserRole coreUserRole) {
		return coreUserRoleRepo.save(coreUserRole);
	}
	
	@Override
	public void deleteById(Long id) {
		coreUserRoleRepo.deleteById(id);
	}
	
	@Override
	public JpaRepository<CoreUserRole, Long> getRepository() {
		return this.coreUserRoleRepo;
	}
	
	@Override
	public Set<String> findActiveRoleCodesByUsernameAndAppCode(String username, String appCode) {
		if (username.isBlank() || appCode.isBlank()) {
			return Collections.emptySet();
		}
		return coreUserRoleRepo.findActiveRoleCodesByUsernameAndAppCode(username, appCode);
	}
	
	@Override
	public Set<String> findAllActiveRoleCodesByUsername(String username) {
		if (username.isBlank()) {
			return Collections.emptySet();
		}
		return coreUserRoleRepo.findAllActiveRoleCodesByUsername(username);
	}
	
	@Override
	public Map<String, Set<String>> findActiveRoleCodesForUsersInApp(Collection<String> usernames, String appCode) {
		if (usernames.isEmpty()) {
			return Collections.emptyMap();
		}
		List<CoreUserRole> assignments = coreUserRoleRepo.findActiveForUsersInApp(usernames, appCode);
		return assignments.stream()
				.collect(Collectors.groupingBy(
						CoreUserRole::getUsername,
						Collectors.mapping(CoreUserRole::getRoleCode, Collectors.toSet())
				                              ));
	}
	
	@Override
	public Map<String, Set<String>> findAllActiveRoleCodesForUsers(Collection<String> usernames) {
		// Tương tự, cần có một phương thức `findActiveForUsers` trong Repo để tối ưu
		// ...
		return Collections.emptyMap(); // Tạm thời
	}
	
	@Override
	public boolean isRoleInUse(CoreRole role) {
		if (role.getCode() == null || role.getAppCode() == null) {
			return false;
		}
		return coreUserRoleRepo.existsByRoleCodeAndAppCode(role.getCode(), role.getAppCode());
	}
}
