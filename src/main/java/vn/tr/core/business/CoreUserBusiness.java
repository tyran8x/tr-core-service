package vn.tr.core.business;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.common.core.exception.ServiceException;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.core.exception.base.PermissionDeniedException;
import vn.tr.common.jpa.helper.AssociationSyncHelper;
import vn.tr.common.jpa.helper.GenericUpsertHelper;
import vn.tr.common.web.data.dto.BulkOperationResult;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.common.web.utils.PagedResult;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.dao.model.CoreUserApp;
import vn.tr.core.dao.model.CoreUserGroup;
import vn.tr.core.dao.model.CoreUserRole;
import vn.tr.core.dao.service.*;
import vn.tr.core.data.criteria.CoreUserSearchCriteria;
import vn.tr.core.data.dto.CoreContactData;
import vn.tr.core.data.dto.CoreUserData;
import vn.tr.core.data.mapper.CoreUserMapper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Lớp Business (Facade) điều phối các nghiệp vụ phức tạp nhất liên quan đến Quản lý Người dùng (CoreUser). Đây là module trung tâm, tương tác với
 * nhiều service khác để đồng bộ hóa toàn bộ thông tin người dùng.
 *
 * @author tyran8x
 * @version 3.0 (Grand Refactoring)
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CoreUserBusiness {
	
	// Primary Services & Mappers
	private final CoreUserService coreUserService;
	private final CoreUserMapper coreUserMapper;
	
	// Association Services
	private final CoreUserAppService coreUserAppService;
	private final CoreUserRoleService coreUserRoleService;
	private final CoreUserGroupService coreUserGroupService;
	private final CoreContactService coreContactService;
	private final CoreTagAssignmentService coreTagAssignmentService;
	
	// Parent Entity Services (for Helpers)
	private final CoreRoleService coreRoleService;
	private final CoreGroupService coreGroupService;
	
	// Helpers & Event Publisher
	private final GenericUpsertHelper genericUpsertHelper;
	private final AssociationSyncHelper associationSyncHelper;
	
	/**
	 * Tạo mới một người dùng.
	 *
	 * @param userData       Dữ liệu người dùng.
	 * @param appCodeContext Ngữ cảnh ứng dụng của người thực hiện. Null nếu là Super Admin.
	 *
	 * @return Dữ liệu người dùng sau khi tạo và đồng bộ.
	 */
	public CoreUserData create(CoreUserData userData, String appCodeContext) {
		validateCreationContext(userData, appCodeContext);
		return upsert(userData, appCodeContext);
	}
	
	private void validateCreationContext(CoreUserData userData, String appCodeContext) {
		if (appCodeContext == null) { // Super Admin
			if (CollUtil.isEmpty(userData.getApps())) {
				throw new ServiceException("Super Admin phải gán ít nhất một ứng dụng cho người dùng mới.");
			}
		} else { // App Admin
			userData.setApps(Set.of(appCodeContext));
		}
	}
	
	/**
	 * Phương thức "upsert" lõi, điều phối việc tạo/cập nhật và đồng bộ hóa tất cả các quan hệ.
	 */
	private CoreUserData upsert(CoreUserData data, String appCodeContext) {
		// 1. Upsert thực thể CoreUser
		CoreUser user = genericUpsertHelper.upsert(
				data,
				() -> coreUserService.findByUsernameIgnoreCaseIncludingDeleted(data.getUsername()),
				() -> coreUserMapper.toEntity(data),
				coreUserMapper::updateEntityFromData,
				coreUserService.getRepository()
		                                          );
		
		// Cập nhật mật khẩu nếu được cung cấp (chỉ khi tạo mới hoặc API đặc biệt)
		if (data.getPassword() != null && !data.getPassword().isBlank()) {
			user.setHashedPassword(BCrypt.hashpw(data.getPassword()));
		}
		CoreUser savedUser = coreUserService.save(user);
		
		// 2. Đồng bộ hóa tất cả các quan hệ liên quan
		syncUserRelations(savedUser, data, appCodeContext);
		
		// 3. (Tùy chọn) Phát ra sự kiện để các service khác đồng bộ
		// UserUpdatedEvent event = new UserUpdatedEvent(this, savedUser, data, appCodeContext);
		// eventPublisher.publishEvent(event);
		
		// 4. Trả về DTO đầy đủ thông tin
		return mapEntityToDataWithRelations(savedUser, appCodeContext);
	}
	
	/**
	 * Đồng bộ hóa tất cả các mối quan hệ của User, sử dụng AssociationSyncHelper và các Domain Service.
	 */
	private void syncUserRelations(CoreUser user, CoreUserData userData, String appCodeContext) {
		String username = user.getUsername();
		boolean isSuperAdmin = (appCodeContext == null);
		
		// --- 1. Đồng bộ User-App (Chỉ Super Admin mới có quyền) ---
		if (isSuperAdmin && userData.getApps() != null) {
			// 2. Định nghĩa ngữ cảnh (Owner Context)
			record UserAppContext(String username, String userTypeCode, LifecycleStatus status) {
			}
			var ownerContext = new UserAppContext(username, userData.getUserTypeCode(), userData.getStatus());
			
			// 3. Gọi helper với đúng 8 tham số và logic phù hợp với model hiện tại
			associationSyncHelper.synchronize(
					// Tham số 1: ownerContext - Ngữ cảnh của "chủ thể"
					ownerContext,
					
					// Tham số 2: existingAssociations - Danh sách liên kết cũ
					coreUserAppService.findByUsernameIncludingDeleted(username),
					
					// Tham số 3: newKeys - Tập hợp các khóa mới (app_code)
					userData.getApps(),
					
					// Tham số 4: keyExtractor - Lấy khóa từ bản ghi cũ (CoreUserApp -> appCode)
					CoreUserApp::getAppCode,
					
					// Tham số 5: associationFactory - Tạo một bản ghi mới rỗng
					CoreUserApp::new,
					
					// Tham số 6: ownerContextSetter - Gán thông tin từ "chủ thể" vào bản ghi mới
					(association, context) -> {
						association.setUsername(context.username());
						association.setUserTypeCode(context.userTypeCode());
						association.setStatus(context.status());
					},
					
					// Tham số 7: keySetter - Gán "khóa" (app_code) vào bản ghi mới
					CoreUserApp::setAppCode,
					
					// Tham số 8: repository - Repository để helper tự lưu/xóa
					coreUserAppService.getRepository());
			
		}
		
		// --- 2. Đồng bộ User-Role & User-Group trong từng App ---
		Set<String> targetAppCodes = isSuperAdmin
				? coreUserAppService.findActiveAppCodesByUsername(username)
				: Set.of(appCodeContext);
		
		for (String appCode : targetAppCodes) {
			// Đồng bộ Role
			if (userData.getRoles() != null) {
				Set<String> rolesForApp = isSuperAdmin
						? coreRoleService.filterExistingRoleCodesInApp(appCode, userData.getRoles())
						: userData.getRoles();
				syncUserRolesInApp(user, appCode, rolesForApp);
			}
			// Đồng bộ Group
			if (userData.getGroups() != null) {
				Set<String> groupsForApp = isSuperAdmin
						? coreGroupService.filterExistingGroupCodesInApp(appCode, userData.getGroups())
						: userData.getGroups();
				syncUserGroupsInApp(user, appCode, groupsForApp);
			}
		}
		
		syncUserContacts(user, appCodeContext, userData.getCoreContactDatas());
		syncUserTags(user, userData.getTagCodes());
	}
	
	private void syncUserContacts(CoreUser user, String appCodeContext, Collection<CoreContactData> contacts) {
		if (contacts != null) {
			coreContactService.synchronizeContactsForOwnerInApp(CoreUser.class.getSimpleName(), user.getUsername(), appCodeContext, contacts);
		}
	}
	
	/**
	 * **HÀM MỚI:** Đóng gói logic đồng bộ hóa thẻ tag cho người dùng.
	 */
	private void syncUserTags(CoreUser user, Set<String> tagCodes) {
		if (tagCodes != null) {
			coreTagAssignmentService.synchronizeTagsForTaggable(CoreUser.class.getSimpleName(), user.getUsername(), tagCodes);
		}
	}
	
	/**
	 * Map Entity sang DTO và tải các quan hệ liên quan.
	 */
	private CoreUserData mapEntityToDataWithRelations(CoreUser user, String appCodeContext) {
		// Tái sử dụng logic của mapEntitiesToDataWithRelationsInBatch để tránh lặp code
		return mapEntitiesToDataWithRelationsInBatch(List.of(user), appCodeContext).getFirst();
	}
	
	private void syncUserRolesInApp(CoreUser user, String appCode, Set<String> roleCodes) {
		record UserRoleContext(String username, String appCode) {
		}
		var ownerContext = new UserRoleContext(user.getUsername(), appCode);
		
		associationSyncHelper.synchronize(
				ownerContext,
				coreUserRoleService.findByUsernameAndAppCodeIncludingDeleted(user.getUsername(), appCode),
				roleCodes,
				CoreUserRole::getRoleCode,
				CoreUserRole::new,
				(association, context) -> {
					association.setUsername(context.username());
					association.setAppCode(context.appCode());
				},
				CoreUserRole::setRoleCode,
				coreUserRoleService.getRepository()
		                                 );
	}
	
	private void syncUserGroupsInApp(CoreUser user, String appCode, Set<String> groupCodes) {
		record UserGroupContext(String username, String appCode) {
		}
		var ownerContext = new UserGroupContext(user.getUsername(), appCode);
		
		// **SỬA LỖI TẠI ĐÂY**
		associationSyncHelper.synchronize(
				ownerContext,
				coreUserGroupService.findByUsernameAndAppCodeIncludingDeleted(user.getUsername(), appCode),
				groupCodes,
				CoreUserGroup::getGroupCode,
				CoreUserGroup::new,
				(association, context) -> {
					association.setUsername(context.username());
					association.setAppCode(context.appCode());
				},
				CoreUserGroup::setGroupCode,
				coreUserGroupService.getRepository()
		                                 );
	}
	
	// =================================================================================================================
	// Private Orchestration & Helper Methods
	// =================================================================================================================
	
	/**
	 * Map một danh sách Entities sang DTOs và tải các quan hệ một cách tối ưu (tránh N+1).
	 */
	private List<CoreUserData> mapEntitiesToDataWithRelationsInBatch(List<CoreUser> users, String appCodeContext) {
		if (users.isEmpty()) return Collections.emptyList();
		
		Set<String> usernames = users.stream().map(CoreUser::getUsername).collect(Collectors.toSet());
		boolean isSuperAdmin = (appCodeContext == null);
		
		// Tải dữ liệu hàng loạt
		Map<String, Set<String>> appsByUser = coreUserAppService.findActiveAppCodesForUsers(usernames);
		Map<String, Set<String>> rolesByUser = isSuperAdmin
				? coreUserRoleService.findAllActiveRoleCodesForUsers(usernames)
				: coreUserRoleService.findActiveRoleCodesForUsersInApp(usernames, appCodeContext);
		Map<String, Set<String>> groupsByUser = isSuperAdmin
				? coreUserGroupService.findAllActiveGroupCodesForUsers(usernames)
				: coreUserGroupService.findActiveGroupCodesForUsersInApp(usernames, appCodeContext);
		
		return users.stream().map(user -> {
			CoreUserData data = coreUserMapper.toData(user);
			String username = user.getUsername();
			data.setApps(appsByUser.getOrDefault(username, Collections.emptySet()));
			data.setRoles(rolesByUser.getOrDefault(username, Collections.emptySet()));
			data.setGroups(groupsByUser.getOrDefault(username, Collections.emptySet()));
			// Tương tự cho Contacts và Tags nếu cần
			return data;
		}).collect(Collectors.toList());
	}
	
	/**
	 * Cập nhật thông tin một người dùng.
	 *
	 * @param id             ID của người dùng cần cập nhật.
	 * @param userData       Dữ liệu mới.
	 * @param appCodeContext Ngữ cảnh ứng dụng của người thực hiện.
	 *
	 * @return Dữ liệu người dùng sau khi cập nhật và đồng bộ.
	 */
	public CoreUserData update(Long id, CoreUserData userData, String appCodeContext) {
		CoreUser user = findUserAndCheckPermission(id, appCodeContext);
		userData.setId(user.getId()); // Đảm bảo DTO có ID để upsert
		userData.setUsername(user.getUsername()); // Đảm bảo DTO có username để upsert
		return upsert(userData, appCodeContext);
	}
	
	/**
	 * Helper tìm người dùng và kiểm tra quyền truy cập của người thực hiện.
	 */
	private CoreUser findUserAndCheckPermission(Long userId, String appCodeContext) {
		CoreUser user = coreUserService.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException(CoreUser.class, userId));
		if (!hasPermission(user.getUsername(), appCodeContext)) {
			throw new PermissionDeniedException("Không có quyền thao tác trên người dùng này.");
		}
		return user;
	}
	
	private boolean hasPermission(String targetUsername, String appCodeContext) {
		if (appCodeContext == null) return true; // Super Admin có mọi quyền
		return coreUserAppService.isUserInApp(targetUsername, appCodeContext);
	}
	
	/**
	 * Xóa một người dùng (xóa mềm).
	 *
	 * @param id             ID của người dùng cần xóa.
	 * @param appCodeContext Ngữ cảnh ứng dụng.
	 */
	public void delete(Long id, String appCodeContext) {
		CoreUser user = findUserAndCheckPermission(id, appCodeContext);
		// Thêm logic kiểm tra không cho xóa chính mình nếu cần
		coreUserService.deleteByIds(Set.of(user.getId()));
	}
	
	/**
	 * Xóa hàng loạt người dùng và trả về kết quả chi tiết.
	 *
	 * @param ids            Collection các ID cần xóa.
	 * @param appCodeContext Ngữ cảnh ứng dụng.
	 *
	 * @return Báo cáo chi tiết về kết quả xóa.
	 */
	public BulkOperationResult<Long> bulkDelete(Collection<Long> ids, String appCodeContext) {
		BulkOperationResult<Long> result = new BulkOperationResult<>();
		if (ids == null || ids.isEmpty()) return result;
		
		List<CoreUser> usersInDb = coreUserService.findAllByIdIn(ids);
		for (CoreUser user : usersInDb) {
			try {
				// Tái sử dụng logic kiểm tra quyền
				if (!hasPermission(user.getUsername(), appCodeContext)) {
					throw new PermissionDeniedException(String.format("Không có quyền xóa người dùng '%s'.", user.getUsername()));
				}
				// Thêm các ràng buộc khác nếu cần, ví dụ: không cho xóa user đang hoạt động
				result.addSuccess(user.getId());
			} catch (Exception e) {
				result.addFailure(user.getId(), e.getMessage());
			}
		}
		
		if (!result.getSuccessfulItems().isEmpty()) {
			coreUserService.deleteByIds(result.getSuccessfulItems());
		}
		return result;
	}
	
	/**
	 * Thay đổi mật khẩu cho một người dùng.
	 */
	public void changePassword(String username, String newPassword, String appCodeContext) {
		CoreUser user = findUserAndCheckPermission(username, appCodeContext);
		user.setHashedPassword(BCrypt.hashpw(newPassword));
		coreUserService.save(user);
	}
	
	private CoreUser findUserAndCheckPermission(String username, String appCodeContext) {
		CoreUser user = coreUserService.findByUsernameIgnoreCase(username)
				.orElseThrow(() -> new EntityNotFoundException(CoreUser.class, username));
		if (!hasPermission(user.getUsername(), appCodeContext)) {
			throw new PermissionDeniedException("Không có quyền thao tác trên người dùng này.");
		}
		return user;
	}
	
	/**
	 * Tìm người dùng theo ID.
	 */
	@Transactional(readOnly = true)
	public CoreUserData findById(Long id, String appCodeContext) {
		CoreUser user = findUserAndCheckPermission(id, appCodeContext);
		return mapEntityToDataWithRelations(user, appCodeContext);
	}
	
	/**
	 * Tìm kiếm và phân trang người dùng.
	 */
	@Transactional(readOnly = true)
	public PagedResult<CoreUserData> findAll(CoreUserSearchCriteria criteria, String appCodeContext) {
		if (appCodeContext != null) {
			criteria.setAppCode(appCodeContext);
		}
		Pageable pageable = CoreUtils.getPageRequest(criteria);
		Page<CoreUser> pageUser = coreUserService.findAll(criteria, pageable);
		return PagedResult.from(pageUser, user -> mapEntityToDataWithRelations(user, appCodeContext));
	}
	
	/**
	 * Lấy danh sách đầy đủ người dùng (không phân trang) theo tiêu chí.
	 *
	 * @param criteria       Các tiêu chí để lọc.
	 * @param appCodeContext Ngữ cảnh ứng dụng để lọc kết quả.
	 *
	 * @return Danh sách đầy đủ người dùng.
	 */
	@Transactional(readOnly = true)
	public List<CoreUserData> getAll(CoreUserSearchCriteria criteria, String appCodeContext) {
		if (appCodeContext != null) {
			criteria.setAppCode(appCodeContext);
		}
		List<CoreUser> users = coreUserService.findAll(criteria);
		// Tái sử dụng helper map hàng loạt để tối ưu hiệu năng
		return mapEntitiesToDataWithRelationsInBatch(users, appCodeContext);
	}
	
	@Transactional
	public void updateStatus(String username, LifecycleStatus newStatus, String appCodeContext) {
		CoreUser user = coreUserService.findFirstByUsernameIgnoreCase(username)
				.orElseThrow(() -> new EntityNotFoundException(CoreUser.class, username));
		
		// Kiểm tra quyền: App Admin chỉ được cập nhật trạng thái người dùng trong app của họ
		hasPermission(username, appCodeContext);
		
		user.setStatus(newStatus);
		coreUserService.save(user);
	}
}
