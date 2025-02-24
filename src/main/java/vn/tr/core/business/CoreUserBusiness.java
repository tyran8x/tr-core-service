package vn.tr.core.business;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.core.utils.FunctionUtils;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.core.dao.model.CoreRole;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.dao.model.CoreUser2Role;
import vn.tr.core.dao.service.CoreRoleService;
import vn.tr.core.dao.service.CoreUser2RoleService;
import vn.tr.core.dao.service.CoreUserService;
import vn.tr.core.data.CoreUserChangeIsEnabledData;
import vn.tr.core.data.CoreUserChangePasswordData;
import vn.tr.core.data.CoreUserData;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoreUserBusiness {
	
	private final CoreUserService coreUserService;
	private final CoreRoleService coreRoleService;
	private final CoreUser2RoleService coreUser2RoleService;
	
	private CoreUserData convertToCoreUserData(CoreUser coreUser, String email) {
		CoreUserData coreUserData = new CoreUserData();
		coreUserData.setId(coreUser.getId());
		coreUserData.setEmail(email);
		coreUserData.setNickName(coreUser.getNickName());
		coreUserData.setUserName(coreUser.getUserName());
		coreUserData.setUserType(coreUser.getUserType());
		coreUserData.setPassword(coreUser.getPassword());
		coreUserData.setIsEnabled(Boolean.TRUE.equals(coreUser.getIsEnabled()));
		List<CoreUser2Role> coreUser2Roles = coreUser2RoleService.findByUserNameAndDaXoaFalse(email);
		Set<String> roles = coreUser2Roles.stream().map(CoreUser2Role::getRole).collect(Collectors.toSet());
		coreUserData.setRoles(roles);
		coreUserData.setAppCode(coreUser.getAppCode());
		return coreUserData;
	}
	
	public CoreUserData create(CoreUserData coreUserData) {
		CoreUser coreUser = new CoreUser();
		return save(coreUser, coreUserData);
	}
	
	public void delete(Long id) throws EntityNotFoundException {
		Optional<CoreUser> optional = coreUserService.findById(id);
		if (optional.isEmpty()) {
			throw new EntityNotFoundException(CoreUser.class, id);
		}
		CoreUser coreUser = optional.get();
		coreUser.setDaXoa(true);
		coreUserService.save(coreUser);
	}
	
	public Page<CoreUserData> findAll(int page, int size, String sortBy, String sortDir, String email, String name, List<String> roles,
			String appCode) {
		Pageable pageable = CoreUtils.getPageRequest(page, size, sortBy, sortDir);
		Page<CoreUser> pageCoreUser = coreUserService.findAll(email, name, roles, appCode, pageable);
		return pageCoreUser.map(x -> convertToCoreUserData(x, x.getEmail()));
	}
	
	public CoreUserData findByEmail(String email) {
		Optional<CoreUser> optionalCoreUser = coreUserService.findFirstByEmailAndDaXoaFalse(email);
		CoreUser coreUser = new CoreUser();
		if (optionalCoreUser.isPresent()) {
			coreUser = optionalCoreUser.get();
		}
		return convertToCoreUserData(coreUser, email);
	}
	
	public CoreUserData findById(Long id) {
		Optional<CoreUser> optionalCoreUser = coreUserService.findById(id);
		if (optionalCoreUser.isEmpty()) {
			throw new EntityNotFoundException(CoreUser.class, id);
		}
		CoreUser coreUser = optionalCoreUser.get();
		return convertToCoreUserData(coreUser, coreUser.getEmail());
	}
	
	public CoreUserData update(Long id, CoreUserData coreUserData) {
		Optional<CoreUser> optional = coreUserService.findById(id);
		if (optional.isEmpty()) {
			throw new EntityNotFoundException(CoreUser.class, id);
		}
		CoreUser coreUser = optional.get();
		return save(coreUser, coreUserData);
	}
	
	private CoreUserData save(CoreUser coreUser, CoreUserData coreUserData) {
		coreUser.setDaXoa(false);
		coreUser.setUserName(FunctionUtils.removeXss(coreUserData.getUserName()));
		coreUser.setEmail(FunctionUtils.removeXss(coreUserData.getEmail()));
		coreUser.setNickName(FunctionUtils.removeXss(coreUserData.getNickName()));
		coreUser.setPhoneNumber(FunctionUtils.removeXss(coreUserData.getPhoneNumber()));
		coreUser.setUserType(coreUserData.getUserType());
		coreUser.setAppCode(FunctionUtils.removeXss(coreUserData.getAppCode()));
		coreUser = coreUserService.save(coreUser);
		Set<String> roles = coreUserData.getRoles();
		List<CoreRole> coreRoles = coreRoleService.findByMaIgnoreCaseInAndDaXoaFalse(roles);
		
		coreUser2RoleService.setFixedDaXoaForUserName(true, coreUser.getUserName());
		if (CollUtil.isNotEmpty(coreRoles)) {
			for (CoreRole coreRole : coreRoles) {
				CoreUser2Role coreUser2Role = new CoreUser2Role();
				Optional<CoreUser2Role> optionalCoreUser2Role = coreUser2RoleService.findFirstByRoleAndUserName(coreRole.getMa(),
						coreUser.getUserName());
				if (optionalCoreUser2Role.isPresent()) {
					coreUser2Role = optionalCoreUser2Role.get();
				}
				coreUser2Role.setDaXoa(false);
				coreUser2Role.setRole(coreRole.getMa());
				coreUser2Role.setUserName(coreUser.getUserName());
				coreUser2RoleService.save(coreUser2Role);
			}
		}
		return convertToCoreUserData(coreUser, coreUser.getEmail());
	}
	
	public void changePassword(CoreUserChangePasswordData coreUserChangePasswordData) {
		Optional<CoreUser> optionalCoreUser = coreUserService.findFirstByEmailAndDaXoaFalse(coreUserChangePasswordData.getUserName());
		if (optionalCoreUser.isEmpty()) {
			throw new EntityNotFoundException(CoreUser.class, coreUserChangePasswordData.getUserName());
		}
		CoreUser coreUser = optionalCoreUser.get();
		coreUser.setPassword(BCrypt.hashpw(coreUserChangePasswordData.getPassword()));
		coreUserService.save(coreUser);
	}
	
	public void changeIsEnabled(CoreUserChangeIsEnabledData coreUserChangeIsEnabledData) {
		Optional<CoreUser> optionalCoreUser = coreUserService.findFirstByEmailAndDaXoaFalse(coreUserChangeIsEnabledData.getUserName());
		if (optionalCoreUser.isEmpty()) {
			throw new EntityNotFoundException(CoreUser.class, coreUserChangeIsEnabledData.getUserName());
		}
		CoreUser coreUser = optionalCoreUser.get();
		coreUser.setIsEnabled(Boolean.TRUE.equals(coreUserChangeIsEnabledData.getIsEnabled()));
		coreUserService.save(coreUser);
	}
	
}
