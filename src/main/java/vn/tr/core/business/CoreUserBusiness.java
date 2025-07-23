package vn.tr.core.business;

import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.common.core.exception.ServiceException;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.core.exception.user.UserException;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.common.web.utils.PagedResult;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.dao.service.*;
import vn.tr.core.data.criteria.CoreUserSearchCriteria;
import vn.tr.core.data.dto.CoreUserData;
import vn.tr.core.data.mapper.CoreContactMapper;
import vn.tr.core.data.mapper.CoreUserMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CoreUserBusiness {
	
	//<editor-fold desc="Dependencies">
	private final CoreUserService coreUserService;
	private final CoreUserAppService coreUserAppService;
	private final CoreUserRoleService coreUserRoleService;
	private final CoreUserGroupService coreUserGroupService;
	private final CoreContactService coreContactService;
	private final CoreTagService coreTagService;
	private final CoreUserMapper coreUserMapper;
	private final CoreContactMapper coreContactMapper;
	//</editor-fold>
	
	@Transactional
	public CoreUserData create(CoreUserData userData, String appCodeContext) {
		if (coreUserService.existsByUsernameIgnoreCase(userData.getUsername())) {
			throw new UserException("user.username.exists", userData.getUsername());
		}
		
		// Super Admin phải chỉ định appCode cho user mới
		// App Admin tự động gán appCode của mình cho user mới
		if (appCodeContext == null) { // Super Admin
			if (CollectionUtils.isEmpty(userData.getApps())) {
				throw new ServiceException("Super Admin must assign at least one app to a new user.");
			}
		} else { // App Admin
			// Đảm bảo user mới được gán cho app của admin, bỏ qua các app khác nếu có
			userData.setApps(Collections.singleton(appCodeContext));
		}
		
		CoreUser user = coreUserMapper.toEntity(userData);
		if (userData.getPassword() != null && !userData.getPassword().isBlank()) {
			user.setHashedPassword(BCrypt.hashpw(userData.getPassword()));
		}
		
		CoreUser savedUser = coreUserService.save(user);
		syncUserRelations(savedUser, userData, appCodeContext);
		
		// Trả về DTO đã được map đầy đủ quan hệ
		return mapEntityToDataWithRelations(savedUser, appCodeContext);
	}
	
	@Transactional
	public CoreUserData update(Long id, CoreUserData userData, String appCodeContext) {
		CoreUser user = coreUserService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreUser.class, id));
		
		// Kiểm tra quyền: App Admin chỉ được cập nhật người dùng trong app của họ
		checkPermission(user.getUsername(), appCodeContext);
		
		userData.setUsername(null); // Không cho phép cập nhật username
		userData.setPassword(null); // Dùng API riêng để đổi mật khẩu
		coreUserMapper.updateEntityFromData(userData, user);
		
		CoreUser savedUser = coreUserService.save(user);
		syncUserRelations(savedUser, userData, appCodeContext);
		
		return mapEntityToDataWithRelations(savedUser, appCodeContext);
	}
	
	@Transactional
	public void delete(Long id, String appCodeContext) {
		CoreUser user = coreUserService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreUser.class, id));
		
		// Kiểm tra quyền: App Admin chỉ được xóa người dùng trong app của họ
		checkPermission(user.getUsername(), appCodeContext);
		
		coreUserService.deleteById(id);
	}
	
	@Transactional
	public void bulkDelete(Set<Long> ids, String appCodeContext) {
		if (CollectionUtils.isEmpty(ids)) {
			return;
		}
		if (appCodeContext != null) { // App Admin
			List<CoreUser> usersToDelete = coreUserService.findAllByIdIn(ids);
			// Lọc ra các ID mà App Admin này có quyền xóa
			Set<Long> allowedIds = usersToDelete.stream()
					.filter(user -> coreUserAppService.isUserInApp(user.getUsername(), appCodeContext))
					.map(CoreUser::getId)
					.collect(Collectors.toSet());
			
			if (!allowedIds.isEmpty()) {
				coreUserService.deleteByIds(allowedIds);
			}
		} else { // Super Admin được phép xóa tất cả
			coreUserService.deleteByIds(ids);
		}
	}
	
	@Transactional
	public void changePassword(String username, String newPassword, String appCodeContext) {
		CoreUser user = coreUserService.findFirstByUsernameIgnoreCase(username)
				.orElseThrow(() -> new EntityNotFoundException(CoreUser.class, username));
		
		// Kiểm tra quyền: App Admin chỉ được đổi mật khẩu người dùng trong app của họ
		checkPermission(username, appCodeContext);
		
		user.setHashedPassword(BCrypt.hashpw(newPassword));
		coreUserService.save(user);
	}
	
	@Transactional
	public void updateStatus(String username, LifecycleStatus newStatus, String appCodeContext) {
		CoreUser user = coreUserService.findFirstByUsernameIgnoreCase(username)
				.orElseThrow(() -> new EntityNotFoundException(CoreUser.class, username));
		
		// Kiểm tra quyền: App Admin chỉ được cập nhật trạng thái người dùng trong app của họ
		checkPermission(username, appCodeContext);
		
		user.setStatus(newStatus);
		coreUserService.save(user);
	}
	
	@Transactional(readOnly = true)
	public CoreUserData findById(Long id, String appCodeContext) {
		CoreUser user = coreUserService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreUser.class, id));
		
		// Kiểm tra quyền: App Admin chỉ được xem người dùng trong app của họ
		checkPermission(user.getUsername(), appCodeContext);
		
		return mapEntityToDataWithRelations(user, appCodeContext);
	}
	
	@Transactional(readOnly = true)
	public CoreUserData findByUsername(String username, String appCodeContext) {
		CoreUser user = coreUserService.findFirstByUsernameIgnoreCase(username)
				.orElseThrow(() -> new EntityNotFoundException(CoreUser.class, username));
		
		// Kiểm tra quyền: App Admin chỉ được xem người dùng trong app của họ
		checkPermission(user.getUsername(), appCodeContext);
		
		return mapEntityToDataWithRelations(user, appCodeContext);
	}
	
	@Transactional(readOnly = true)
	public PagedResult<CoreUserData> findAll(CoreUserSearchCriteria criteria, String appCodeContext) {
		// Gán appCode vào criteria để lọc ở tầng DAO nếu người dùng là App Admin
		if (appCodeContext != null) {
			criteria.setAppCode(appCodeContext);
		}
		
		Pageable pageable = CoreUtils.getPageRequest(criteria.getPage(), criteria.getSize(), criteria.getSortBy(), criteria.getSortDir());
		Page<CoreUser> pageUser = coreUserService.findAll(criteria, pageable);
		
		List<CoreUserData> userDataList = mapEntitiesToDataWithRelationsInBatch(pageUser.getContent(), appCodeContext);
		
		return new PagedResult<>(userDataList, pageUser.getTotalElements(), pageUser.getNumber(), pageUser.getSize());
	}
	
	@Transactional(readOnly = true)
	public List<CoreUserData> getAll(CoreUserSearchCriteria criteria, String appCodeContext) {
		if (appCodeContext != null) {
			criteria.setAppCode(appCodeContext);
		}
		List<CoreUser> users = coreUserService.findAll(criteria);
		return mapEntitiesToDataWithRelationsInBatch(users, appCodeContext);
	}
	
	// =================================================================================================================
	// Helper Methods
	// =================================================================================================================
	
	/**
	 * Đồng bộ các mối quan hệ của User (Apps, Roles, Groups, Contacts, Tags).
	 */
	private void syncUserRelations(CoreUser user, CoreUserData userData, String appCodeContext) {
		String username = user.getUsername();
		boolean isSuperAdminContext = (appCodeContext == null);
		
		if (isSuperAdminContext) {
			// Super Admin đồng bộ trên nhiều app dựa vào payload
			if (userData.getApps() != null) {
				coreUserAppService.synchronizeUserApps(username, userData.getApps());
			}
			
			Set<String> allRelevantAppCodes = coreUserAppService.findActiveAppCodesByUsername(username);
			
			for (String appCode : allRelevantAppCodes) {
				Set<String> rolesForApp = (userData.getRoles() == null) ? Collections.emptySet() :
						userData.getRoles().stream().filter(r -> r.startsWith("ROLE_" + appCode)).collect(Collectors.toSet());
				coreUserRoleService.synchronizeUserRolesInApp(username, appCode, rolesForApp);
				
				Set<String> groupsForApp = (userData.getGroups() == null) ? Collections.emptySet() :
						userData.getGroups().stream().filter(g -> g.startsWith("GROUP_" + appCode)).collect(Collectors.toSet());
				coreUserGroupService.synchronizeUserGroupsInApp(username, appCode, groupsForApp);
			}
		} else {
			// App Admin chỉ đồng bộ trong phạm vi app của mình
			if (userData.getRoles() != null) {
				coreUserRoleService.synchronizeUserRolesInApp(username, appCodeContext, userData.getRoles());
			}
			if (userData.getGroups() != null) {
				coreUserGroupService.synchronizeUserGroupsInApp(username, appCodeContext, userData.getGroups());
			}
		}
		
		// Đồng bộ Contact và Tag có thể dùng chung logic
		if (userData.getCoreContactDatas() != null) {
			coreContactService.synchronizeContactsForOwnerInApp(CoreUser.class.getSimpleName(), username, appCodeContext, user.getEmail(),
					userData.getCoreContactDatas());
		}
		if (userData.getCoreTagAssignmentDatas() != null) {
			coreTagService.synchronizeTagsForTaggable(CoreUser.class.getSimpleName(), username, userData.getCoreTagAssignmentDatas());
		}
	}
	
	/**
	 * Kiểm tra quyền hạn của một người dùng dựa trên appCodeContext. Ném ra UserException nếu không có quyền.
	 */
	private void checkPermission(String username, String appCodeContext) {
		// Nếu appCodeContext là null, đó là Super Admin, luôn có quyền.
		if (appCodeContext == null) {
			return;
		}
		// Nếu là App Admin, kiểm tra user có thuộc app của họ không.
		if (!coreUserAppService.isUserInApp(username, appCodeContext)) {
			// Cần thêm phương thức isUserInApp vào CoreUserAppService
			throw new UserException("user.permission.denied.cross_app", username, appCodeContext);
		}
	}
	
	/**
	 * Map một User Entity sang User DTO và tải các mối quan hệ liên quan.
	 */
	private CoreUserData mapEntityToDataWithRelations(CoreUser user, String appCodeContext) {
		CoreUserData data = coreUserMapper.toData(user);
		boolean isSuperAdminContext = (appCodeContext == null);
		String username = user.getUsername();
		
		data.setApps(coreUserAppService.findActiveAppCodesByUsername(username));
		
		if (isSuperAdminContext) {
			data.setRoles(coreUserRoleService.findAllActiveRoleCodesByUsername(username));
			data.setGroups(coreUserGroupService.findAllActiveGroupCodesByUsername(username));
			data.setCoreContactDatas(coreContactMapper.toData(coreContactService.findAllActiveByOwner(CoreUser.class.getSimpleName(), username)));
		} else {
			data.setRoles(coreUserRoleService.findActiveRoleCodesByUsernameAndAppCode(username, appCodeContext));
			data.setGroups(coreUserGroupService.findActiveGroupCodesByUsernameAndAppCode(username, appCodeContext));
			data.setCoreContactDatas(
					coreContactMapper.toData(coreContactService.findActiveByOwnerInApp(CoreUser.class.getSimpleName(), username, appCodeContext)));
		}
		return data;
	}
	
	/**
	 * Phiên bản tối ưu của việc map, giải quyết vấn đề N+1 query.
	 */
	private List<CoreUserData> mapEntitiesToDataWithRelationsInBatch(List<CoreUser> users, String appCodeContext) {
		if (users.isEmpty()) {
			return Collections.emptyList();
		}
		
		Set<String> usernames = users.stream().map(CoreUser::getUsername).collect(Collectors.toSet());
		boolean isSuperAdminContext = (appCodeContext == null);
		
		// 1. Tải tất cả dữ liệu liên quan trong một vài query
		Map<String, Set<String>> appsByUser = coreUserAppService.findActiveAppCodesForUsers(usernames);
		Map<String, Set<String>> rolesByUser;
		Map<String, Set<String>> groupsByUser;
		
		if (isSuperAdminContext) {
			rolesByUser = coreUserRoleService.findAllActiveRoleCodesForUsers(usernames);
			groupsByUser = coreUserGroupService.findAllActiveGroupCodesForUsers(usernames);
		} else {
			rolesByUser = coreUserRoleService.findActiveRoleCodesForUsersInApp(usernames, appCodeContext);
			groupsByUser = coreUserGroupService.findActiveGroupCodesForUsersInApp(usernames, appCodeContext);
		}
		
		// Cần thêm các phương thức batch-fetching (ForUsers, ForUsersInApp) vào các Service tương ứng.
		// Ví dụ:
		// interface CoreUserAppService {
		//     boolean isUserInApp(String username, String appCode);
		//     Map<String, Set<String>> findActiveAppCodesForUsers(Set<String> usernames);
		// }
		
		// 2. Map dữ liệu
		return users.stream().map(user -> {
			CoreUserData data = coreUserMapper.toData(user);
			String username = user.getUsername();
			
			data.setApps(appsByUser.getOrDefault(username, Collections.emptySet()));
			data.setRoles(rolesByUser.getOrDefault(username, Collections.emptySet()));
			data.setGroups(groupsByUser.getOrDefault(username, Collections.emptySet()));
			// Tương tự cho Contacts và Tags nếu cần tối ưu
			
			return data;
		}).collect(Collectors.toList());
	}
}
