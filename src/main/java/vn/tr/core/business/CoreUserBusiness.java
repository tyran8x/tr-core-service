package vn.tr.core.business;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.core.utils.FunctionUtils;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.core.dao.model.CoreRole;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.dao.model.CoreUserRole;
import vn.tr.core.dao.service.CoreRoleService;
import vn.tr.core.dao.service.CoreUserRoleService;
import vn.tr.core.dao.service.CoreUserService;
import vn.tr.core.data.CoreUserChangeIsEnabledData;
import vn.tr.core.data.CoreUserChangePasswordData;
import vn.tr.core.data.dto.CoreUserData;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoreUserBusiness {
	
	private final CoreUserService coreUserService;
	private final CoreRoleService coreRoleService;
	private final CoreUserRoleService coreUserRoleService;
	
	private CoreUserData convertToCoreUserData(CoreUser coreUser) {
		CoreUserData coreUserData = new CoreUserData();
		coreUserData.setId(coreUser.getId());
		//	coreUserData.setEmail(email);
		coreUserData.setFullName(coreUser.getFullName());
		coreUserData.setUsername(coreUser.getUsername());
		List<CoreUserRole> coreUserRoles = coreUserRoleService.findByUserNameAndDaXoaFalse(coreUser.getUsername());
		Set<String> roles = coreUserRoles.stream().map(CoreUserRole::getRoleCode).collect(Collectors.toSet());
		coreUserData.setRoles(roles);
		//	List<CoreUserConnect> coreUserConnects = coreUserConnectService.findByUserNameIgnoreCaseAndDaXoaFalse(coreUser.getUsername());
//		Map<String, String> connects = new HashMap<>();
//		if (CollUtil.isNotEmpty(coreUserConnects)) {
//			for (CoreUserConnect coreUserConnect : coreUserConnects) {
//				connects.put(coreUserConnect.getAppName(), coreUserConnect.getAppUserId());
//			}
//		}
//		connects.putIfAbsent("mail", coreUser.getEmail());
//
//		coreUserData.setConnects(connects);
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
	
	public Page<CoreUserData> findAll(int page, int size, String sortBy, String sortDir, String search, String email, String name, List<String> roles,
			String appCode) {
		Pageable pageable = CoreUtils.getPageRequest(page, size, sortBy, sortDir);
		Page<CoreUser> pageCoreUser = coreUserService.findAll(search, email, name, roles, appCode, pageable);
		return pageCoreUser.map(this::convertToCoreUserData);
	}
	
	public CoreUserData findByEmail(String email) {
		Optional<CoreUser> optionalCoreUser = coreUserService.findFirstByUsernameAndDaXoaFalse(email);
		CoreUser coreUser = new CoreUser();
		if (optionalCoreUser.isPresent()) {
			coreUser = optionalCoreUser.get();
		}
		return convertToCoreUserData(coreUser);
	}
	
	public CoreUserData findById(Long id) {
		Optional<CoreUser> optionalCoreUser = coreUserService.findById(id);
		if (optionalCoreUser.isEmpty()) {
			throw new EntityNotFoundException(CoreUser.class, id);
		}
		CoreUser coreUser = optionalCoreUser.get();
		return convertToCoreUserData(coreUser);
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
		coreUser.setUsername(FunctionUtils.removeXss(coreUserData.getUsername()));
		coreUser.setFullName(FunctionUtils.removeXss(coreUserData.getFullName()));
//		coreUser.setUserTypeId(coreUserData.getUserType());
//		coreUser.setIsEnabled(Boolean.TRUE.equals(coreUserData.getIsEnabled()));
		if (Objects.isNull(coreUser.getId())) {
			coreUser.setHashedPassword(BCrypt.hashpw(coreUserData.getPassword()));
		}
		coreUser = coreUserService.save(coreUser);
		Set<String> roles = coreUserData.getRoles();
		List<CoreRole> coreRoles = coreRoleService.findByMaIgnoreCaseInAndDaXoaFalse(roles);
		
		coreUserRoleService.setFixedDaXoaForUserName(true, coreUser.getUsername());
		if (CollUtil.isNotEmpty(coreRoles)) {
			for (CoreRole coreRole : coreRoles) {
				CoreUserRole coreUserRole = new CoreUserRole();
				Optional<CoreUserRole> optionalCoreUserRole = coreUserRoleService.findFirstByRoleAndUserName(coreRole.getCode(),
						coreUser.getUsername());
				if (optionalCoreUserRole.isPresent()) {
					coreUserRole = optionalCoreUserRole.get();
				}
				coreUserRole.setDaXoa(false);
				coreUserRole.setRoleCode(coreRole.getCode());
				coreUserRole.setUsername(coreUser.getUsername());
				coreUserRoleService.save(coreUserRole);
			}
		}
		return convertToCoreUserData(coreUser);
	}
	
	public void changePassword(CoreUserChangePasswordData coreUserChangePasswordData) {
		Optional<CoreUser> optionalCoreUser = coreUserService.findFirstByUsernameAndDaXoaFalse(coreUserChangePasswordData.getUsername());
		if (optionalCoreUser.isEmpty()) {
			throw new EntityNotFoundException(CoreUser.class, coreUserChangePasswordData.getUsername());
		}
		CoreUser coreUser = optionalCoreUser.get();
		coreUser.setHashedPassword(BCrypt.hashpw(coreUserChangePasswordData.getPassword()));
		coreUserService.save(coreUser);
	}
	
	public void changeIsEnabled(CoreUserChangeIsEnabledData coreUserChangeIsEnabledData) {
		Optional<CoreUser> optionalCoreUser = coreUserService.findFirstByUsernameAndDaXoaFalse(coreUserChangeIsEnabledData.getUsername());
		if (optionalCoreUser.isEmpty()) {
			throw new EntityNotFoundException(CoreUser.class, coreUserChangeIsEnabledData.getUsername());
		}
		CoreUser coreUser = optionalCoreUser.get();
		//coreUser.setStatus(Boolean.TRUE.equals(coreUserChangeIsEnabledData.getIsEnabled()));
		coreUserService.save(coreUser);
	}
	
}
