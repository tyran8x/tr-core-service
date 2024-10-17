package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.tr.core.dao.model.CoreUser2Role;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CorePermissionServiceImpl implements CorePermissionService {

	private final CoreUser2RoleService coreUser2RoleService;
	private final CoreRole2MenuService coreRole2MenuService;

	@Override
	public Set<String> getRolePermission(String userName) {
		List<CoreUser2Role> coreUser2Roles = coreUser2RoleService.findByUserNameAndDaXoaFalse(userName);
		return new HashSet<>(coreUser2Roles.stream().map(CoreUser2Role::getRole).toList());
	}

	@Override
	public Set<String> getMenuPermission(String userName) {
		return Set.of();
	}
}
