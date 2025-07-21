package vn.tr.core.business;

import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.core.exception.user.UserException;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.common.web.utils.PagedResult;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.dao.service.*;
import vn.tr.core.data.criteria.CoreUserSearchCriteria;
import vn.tr.core.data.dto.CoreContactData;
import vn.tr.core.data.dto.CoreUserData;
import vn.tr.core.data.mapper.CoreContactMapper;
import vn.tr.core.data.mapper.CoreUserMapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CoreUserBusiness {
	
	// --- Dependencies ---
	private final CoreUserService coreUserService;
	private final CoreUserAppService coreUserAppService;
	private final CoreUserRoleService coreUserRoleService;
	private final CoreUserGroupService coreUserGroupService;
	private final CoreContactService coreContactService;
	private final CoreUserMapper coreUserMapper;
	private final CoreContactMapper coreContactMapper;
	
	public CoreUserData create(CoreUserData userData, String appCodeContext) {
		if (coreUserService.existsByUsernameIgnoreCase(userData.getUsername())) {
			throw new UserException("user.not.exists", userData.getUsername());
		}
		CoreUser user = coreUserMapper.toEntity(userData);
		if (userData.getPassword() != null && !userData.getPassword().isBlank()) {
			user.setHashedPassword(BCrypt.hashpw(userData.getPassword()));
		}
		return saveAndSync(user, userData, appCodeContext);
	}
	
	private CoreUserData saveAndSync(CoreUser user, CoreUserData userData, String appCodeContext) {
		CoreUser savedUser = coreUserService.save(user);
		syncUserRelations(savedUser, userData, appCodeContext);
		return findById(savedUser.getId(), appCodeContext);
	}
	
	private void syncUserRelations(CoreUser user, CoreUserData userData, String appCodeContext) {
		boolean isSuperAdmin = (appCodeContext == null);
		
		if (isSuperAdmin) {
			syncAllRelationsForSuperAdmin(user, userData);
		} else {
			syncScopedRelationsForAppAdmin(user, userData, appCodeContext);
		}
	}
	
	@Transactional(readOnly = true)
	public CoreUserData findById(Long id, String appCodeContext) {
		return coreUserService.findById(id)
				.map(user -> mapEntityToDataWithRelations(user, appCodeContext))
				.orElseThrow(() -> new EntityNotFoundException(CoreUser.class, id));
	}
	
	// ... các phương thức khác như delete, changePassword ...
	
	// --- Private Orchestration & Mapping Logic ---
	
	private void syncAllRelationsForSuperAdmin(CoreUser user, CoreUserData userData) {
		String username = user.getUsername();
		
		// Đồng bộ App
		if (userData.getApps() != null) {
			coreUserAppService.synchronizeUserApps(username, userData.getApps());
		}
		
		// Lấy tất cả các app mà user đang hoặc sẽ có quyền để làm phạm vi đồng bộ
		Set<String> allRelevantAppCodes = new HashSet<>(coreUserAppService.findActiveAppCodesByUsername(username));
		if (userData.getApps() != null) {
			allRelevantAppCodes.addAll(userData.getApps());
		}
		
		// Đồng bộ cho từng app
		for (String appCode : allRelevantAppCodes) {
			Set<String> rolesForThisApp = filterAndExtractCodes(userData.getRoles(), appCode, "ROLE");
			coreUserRoleService.synchronizeUserRolesInApp(username, appCode, rolesForThisApp);
			
			Set<String> groupsForThisApp = filterAndExtractCodes(userData.getGroups(), appCode, "GROUP");
			coreUserGroupService.synchronizeUserGroupsInApp(username, appCode, groupsForThisApp);
			
			if (userData.getContacts() != null) {
				List<CoreContactData> contactsForThisApp = userData.getContacts().stream()
						.filter(c -> appCode.equals(c.getAppCode())).collect(Collectors.toList());
				coreContactService.synchronizeContactsForOwnerInApp(
						CoreUser.class.getSimpleName(), username, appCode, user.getEmail(), contactsForThisApp
				                                                   );
			}
		}
	}
	
	private void syncScopedRelationsForAppAdmin(CoreUser user, CoreUserData userData, String appCodeContext) {
		String username = user.getUsername();
		if (userData.getRoles() != null) {
			coreUserRoleService.synchronizeUserRolesInApp(username, appCodeContext, userData.getRoles());
		}
		if (userData.getGroups() != null) {
			coreUserGroupService.synchronizeUserGroupsInApp(username, appCodeContext, userData.getGroups());
		}
		if (userData.getContacts() != null) {
			coreContactService.synchronizeContactsForOwnerInApp(
					CoreUser.class.getSimpleName(), username, appCodeContext, user.getEmail(), userData.getContacts()
			                                                   );
		}
	}
	
	private CoreUserData mapEntityToDataWithRelations(CoreUser user, String appCodeContext) {
		CoreUserData data = coreUserMapper.toData(user);
		boolean isSuperAdmin = (appCodeContext == null);
		
		data.setApps(coreUserAppService.findActiveAppCodesByUsername(user.getUsername()));
		
		if (isSuperAdmin) {
			data.setRoles(coreUserRoleService.findAllActiveRoleCodesByUsername(user.getUsername()));
			data.setGroups(coreUserGroupService.findAllActiveGroupCodesByUsername(user.getUsername()));
			data.setContacts(coreContactMapper.toData(coreContactService.findAllActiveByOwner(CoreUser.class.getSimpleName(), user.getUsername())));
		} else {
			data.setRoles(coreUserRoleService.findActiveRoleCodesByUsernameAndAppCode(user.getUsername(), appCodeContext));
			data.setGroups(coreUserGroupService.findActiveGroupCodesByUsernameAndAppCode(user.getUsername(), appCodeContext));
			data.setContacts(coreContactMapper.toData(
					coreContactService.findActiveByOwnerInApp(CoreUser.class.getSimpleName(), user.getUsername(), appCodeContext)));
		}
		
		return data;
	}
	
	private Set<String> filterAndExtractCodes(Set<String> allCodes, String appCode, String prefix) {
		if (allCodes == null) return Collections.emptySet();
		return allCodes.stream()
				.filter(code -> isOfApp(code, appCode, prefix))
				.collect(Collectors.toSet());
	}
	
	private boolean isOfApp(String code, String appCode, String prefix) {
		if (code == null || appCode == null) return false;
		String[] parts = code.split("_");
		// Quy ước: {PREFIX}_{APP_CODE}_{SUFFIX}
		return parts.length >= 2 && prefix.equalsIgnoreCase(parts[0]) && appCode.equalsIgnoreCase(parts[1]);
	}
	
	// --- Private Helper Methods ---
	
	public CoreUserData update(Long id, CoreUserData userData, String appCodeContext) {
		CoreUser user = coreUserService.findByIdEvenIfDeleted(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreUser.class, id));
		
		userData.setUsername(null); // Không cho phép cập nhật username
		userData.setPassword(null); // Dùng API riêng để đổi mật khẩu
		coreUserMapper.updateEntityFromData(userData, user);
		
		return saveAndSync(user, userData, appCodeContext);
	}
	
	@Transactional(readOnly = true)
	public PagedResult<CoreUserData> findAll(CoreUserSearchCriteria criteria, String appCodeContext) {
		// App Admin chỉ được tìm kiếm người dùng trong app của họ.
		// Super Admin có thể tùy ý lọc theo appCode trong criteria.
		if (appCodeContext != null) {
			log.debug("findAll App Admin scope: applying appCode '{}' to search criteria.", appCodeContext);
			criteria.setAppCode(appCodeContext);
		}
		
		Pageable pageable = CoreUtils.getPageRequest(criteria.getPage(), criteria.getSize(), criteria.getSortBy(), criteria.getSortDir());
		Page<CoreUser> pageUser = coreUserService.findAll(criteria, pageable);
		
		// Luôn truyền appCodeContext vào hàm map
		return PagedResult.from(pageUser, user -> mapEntityToDataWithRelations(user, appCodeContext));
	}
	
	public void changePassword(String username, String newPassword) {
		String normalizedUsername = username.toLowerCase();
		CoreUser user = coreUserService.findFirstByUsernameIgnoreCase(normalizedUsername)
				.orElseThrow(() -> new EntityNotFoundException(CoreUser.class, username));
		user.setHashedPassword(BCrypt.hashpw(newPassword));
		coreUserService.save(user);
	}
	
	@Transactional(readOnly = true)
	public List<CoreUserData> getAll(CoreUserSearchCriteria criteria, String appCodeContext) {
		if (appCodeContext != null) {
			log.debug("getAll App Admin scope: applying appCode '{}' to search criteria.", appCodeContext);
			criteria.setAppCode(appCodeContext);
		}
		List<CoreUser> pageCoreRole = coreUserService.findAll(criteria);
		return pageCoreRole.stream().map(user -> mapEntityToDataWithRelations(user, appCodeContext)).collect(Collectors.toList());
	}
	
	public void delete(Long id) {
		if (!coreUserService.existsById(id)) {
			throw new EntityNotFoundException(CoreUser.class, id);
		}
		coreUserService.deleteById(id);
	}
	
	public void bulkDelete(Set<Long> ids) {
		coreUserService.deleteByIds(ids);
	}
	
	@Transactional(readOnly = true)
	public CoreUserData findByUsername(String username, String appCodeContext) {
		String normalizedUsername = username.toLowerCase();
		return coreUserService.findFirstByUsernameIgnoreCase(normalizedUsername)
				.map(user -> mapEntityToDataWithRelations(user, appCodeContext))
				.orElseThrow(() -> new EntityNotFoundException(CoreUser.class, username));
	}
	
	public void updateStatus(String username, LifecycleStatus newStatus) {
		String normalizedUsername = username.toLowerCase();
		CoreUser user = coreUserService.findFirstByUsernameIgnoreCase(normalizedUsername)
				.orElseThrow(() -> new EntityNotFoundException(CoreUser.class, username));
		user.setStatus(newStatus);
		coreUserService.save(user);
	}
}
