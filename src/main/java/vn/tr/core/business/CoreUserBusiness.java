package vn.tr.core.business;

import cn.dev33.satoken.secure.BCrypt;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.core.dao.model.CoreContact;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.dao.service.*;
import vn.tr.core.data.criteria.CoreUserSearchCriteria;
import vn.tr.core.data.dto.CoreContactData;
import vn.tr.core.data.dto.CoreUserData;
import vn.tr.core.data.mapper.CoreContactMapper;
import vn.tr.core.data.mapper.CoreUserMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CoreUserBusiness {
	
	// --- Dependencies ---
	private final CoreUserService coreUserService;
	private final CoreUserTypeService coreUserTypeService;
	private final CoreContactService coreContactService;
	private final CoreUserRoleService coreUserRoleService;
	private final CoreUserGroupService coreUserGroupService;
	private final CoreUserAppService coreUserAppService;
	private final CoreUserMapper coreUserMapper;
	private final CoreContactMapper coreContactMapper;
	
	public CoreUserData create(CoreUserData userData) {
		String normalizedUsername = userData.getUsername().toLowerCase();
		userData.setUsername(normalizedUsername);
		
		CoreUser user = coreUserMapper.toEntity(userData);
		user.setHashedPassword(BCrypt.hashpw(userData.getPassword()));
		
		return save(user, userData);
	}
	
	private CoreUserData save(CoreUser user, CoreUserData userData) {
		CoreUser savedUser = coreUserService.save(user);
		String username = savedUser.getUsername();
		
		syncUserRelations(username, userData);
		syncUserContacts(username, savedUser.getEmail(), userData.getContacts());
		
		return findByUsername(username);
	}
	
	private void syncUserRelations(String username, CoreUserData userData) {
		String currentAppCode = LoginHelper.getAppCode();
		
		// Đồng bộ App
		coreUserAppService.replaceUserApps(username, userData.getApps());
		
		// Đồng bộ Role (chỉ trong phạm vi app hiện tại)
		Set<String> rolesForCurrentApp = (userData.getRoles() == null)
				? Collections.emptySet()
				: userData.getRoles().stream()
				.filter(roleCode -> isRoleOfApp(roleCode, currentAppCode))
				.collect(Collectors.toSet());
		coreUserRoleService.replaceUserRolesForApp(username, currentAppCode, rolesForCurrentApp);
		
		// Đồng bộ Group (giả định Group là toàn cục)
		coreUserGroupService.replaceUserGroups(username, userData.getGroups());
	}
	
	private void syncUserContacts(String username, String primaryEmail, List<CoreContactData> newContactDataList) {
		final String ownerType = "CORE_USER";
		List<CoreContact> existingContacts = coreContactService.findAllByOwnerIncludingDeleted(ownerType, username);
		Map<Long, CoreContact> existingContactsMapById = existingContacts.stream()
				.collect(Collectors.toMap(CoreContact::getId, Function.identity()));
		
		final List<CoreContactData> finalNewContacts = (newContactDataList != null) ? newContactDataList : Collections.emptyList();
		
		List<CoreContact> toSave = new ArrayList<>();
		Set<Long> processedIds = new HashSet<>();
		
		finalNewContacts.stream()
				.filter(dto -> dto.getId() != null && existingContactsMapById.containsKey(dto.getId()))
				.forEach(dto -> {
					CoreContact existingContact = existingContactsMapById.get(dto.getId());
					coreContactMapper.updateEntityFromData(dto, existingContact);
					existingContact.setDeletedAt(null); // Khôi phục nếu cần
					toSave.add(existingContact);
					processedIds.add(dto.getId());
				});
		
		// Xóa mềm các contact không còn trong danh sách mới
		existingContacts.stream()
				.filter(ec -> ec.getDeletedAt() == null && !processedIds.contains(ec.getId()))
				.forEach(ec -> {
					// Đảm bảo không xóa mềm email chính
					if (!Boolean.TRUE.equals(ec.getIsPrimary())) {
						ec.setDeletedAt(LocalDateTime.now());
						toSave.add(ec);
					}
				});
		
		// Thêm các contact hoàn toàn mới
		finalNewContacts.stream()
				.filter(dto -> dto.getId() == null)
				.map(coreContactMapper::toEntity)
				.peek(contact -> {
					contact.setOwnerValue(username);
					contact.setOwnerType(ownerType);
				})
				.forEach(toSave::add);
		
		if (!toSave.isEmpty()) {
			coreContactService.saveAll(toSave);
		}
		
		ensurePrimaryEmailContactExists(username, primaryEmail);
	}
	
	@Transactional(readOnly = true)
	public CoreUserData findByUsername(String username) {
		String normalizedUsername = username.toLowerCase();
		return coreUserService.findFirstByUsernameIgnoreCase(normalizedUsername)
				.map(this::mapEntityToDataWithRelations)
				.orElseThrow(() -> new EntityNotFoundException(CoreUser.class, username));
	}
	
	private boolean isRoleOfApp(String roleCode, String appCode) {
		if (roleCode == null || appCode == null) return false;
		// Quy ước: ROLE_{APP_CODE}_{SUFFIX}
		String[] parts = roleCode.split("_");
		return parts.length >= 3 && "ROLE".equals(parts[0]) && appCode.equalsIgnoreCase(parts[1]);
	}
	
	private void ensurePrimaryEmailContactExists(String username, String primaryEmail) {
		coreContactService.findPrimaryEmailByOwner("CORE_USER", username)
				.ifPresentOrElse(
						primaryContact -> {
							if (!primaryContact.getValue().equals(primaryEmail)) {
								primaryContact.setValue(primaryEmail);
								coreContactService.save(primaryContact);
							}
						},
						() -> {
							CoreContact newPrimaryContact = CoreContact.builder()
									.ownerValue(username)
									.ownerType("CORE_USER")
									.contactType("EMAIL")
									.value(primaryEmail)
									.isPrimary(true)
									.build();
							coreContactService.save(newPrimaryContact);
						});
	}
	
	private CoreUserData mapEntityToDataWithRelations(CoreUser user) {
		CoreUserData data = coreUserMapper.toData(user);
		
		data.setContacts(coreContactService.findActiveByOwner("CORE_USER", user.getUsername())
				.stream().map(coreContactMapper::toData).collect(Collectors.toList()));
		
		data.setApps(coreUserAppService.findAppCodesByUsername(user.getUsername()));
		data.setRoles(coreUserRoleService.findRoleCodesByUsername(user.getUsername()));
		data.setGroups(coreUserGroupService.findGroupCodesByUsername(user.getUsername()));
		
		return data;
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
	public Page<CoreUserData> findAll(CoreUserSearchCriteria criteria) {
		Pageable pageable = CoreUtils.getPageRequest(criteria.getPage(), criteria.getSize(), criteria.getSortBy(), criteria.getSortDir());
		if (criteria.getUsername() != null) {
			criteria.setUsername(criteria.getUsername().toLowerCase());
		}
		Page<CoreUser> pageUser = coreUserService.findAll(criteria, pageable);
		return pageUser.map(this::mapEntityToDataWithRelations);
	}
	
	@Transactional(readOnly = true)
	public List<CoreUserData> getAll(CoreUserSearchCriteria criteria) {
		List<CoreUser> pageCoreRole = coreUserService.findAll(criteria);
		return pageCoreRole.stream().map(this::mapEntityToDataWithRelations).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public CoreUserData findById(Long id) {
		return coreUserService.findById(id)
				.map(this::mapEntityToDataWithRelations)
				.orElseThrow(() -> new EntityNotFoundException(CoreUser.class, id));
	}
	
	public void changePassword(String username, String newPassword) {
		String normalizedUsername = username.toLowerCase();
		CoreUser user = coreUserService.findFirstByUsernameIgnoreCase(normalizedUsername)
				.orElseThrow(() -> new EntityNotFoundException(CoreUser.class, username));
		user.setHashedPassword(BCrypt.hashpw(newPassword));
		coreUserService.save(user);
	}
	
	public CoreUserData update(Long id, CoreUserData userData) {
		CoreUser user = coreUserService.findById(id).orElseThrow(() -> new EntityNotFoundException(CoreUser.class, id));
		coreUserMapper.updateEntityFromData(userData, user);
		return save(user, userData);
	}
	
	public void updateStatus(String username, LifecycleStatus newStatus) {
		String normalizedUsername = username.toLowerCase();
		CoreUser user = coreUserService.findFirstByUsernameIgnoreCase(normalizedUsername)
				.orElseThrow(() -> new EntityNotFoundException(CoreUser.class, username));
		user.setStatus(newStatus);
		coreUserService.save(user);
	}
}
