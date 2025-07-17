package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CorePermissionServiceImpl implements CorePermissionService {
	
	private final CoreUserRoleService coreUserRoleService;
	private final CoreRolePermissionService coreRolePermissionService;
	
	@Override
	public Set<String> getRolePermission(String userName) {
		//List<CoreUserRole> coreUserRoles = coreUserRoleService.findByUserNameAndDaXoaFalse(userName);
		return new HashSet<>();//(coreUserRoles.stream().map(CoreUserRole::getRoleCode).toList());
	}
	
	@Override
	public Set<String> getMenuPermission(String userName) {
		//List<CoreUserRole> coreUserRoles = coreUserRoleService.findByUserNameAndDaXoaFalse(userName);
		return new HashSet<>();//(coreUserRoles.stream().map(CoreUserRole::getRoleCode).toList());
	}
}
