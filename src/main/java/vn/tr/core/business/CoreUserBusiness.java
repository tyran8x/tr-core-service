package vn.tr.core.business;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.core.utils.FunctionUtils;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.core.dao.model.CoreContact;
import vn.tr.core.dao.model.CoreRole;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.dao.model.CoreUserRole;
import vn.tr.core.dao.service.*;
import vn.tr.core.data.CoreUserChangeIsEnabledData;
import vn.tr.core.data.CoreUserChangePasswordData;
import vn.tr.core.data.criteria.CoreUserSearchCriteria;
import vn.tr.core.data.dto.CoreContactData;
import vn.tr.core.data.dto.CoreUserData;
import vn.tr.core.data.mapper.CoreUserMapper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoreUserBusiness {
	
	private final CoreUserService coreUserService;
	private final CoreRoleService coreRoleService;
	private final CoreUserRoleService coreUserRoleService;
	private final CoreUserAppService coreUserAppService;
	private final CoreUserGroupService coreUserGroupService;
	private final CoreUserMapper coreUserMapper;
	
	private CoreUserData convertToCoreUserData(CoreUser coreUser) {
		CoreUserData coreUserData = new CoreUserData();
		coreUserData.setId(coreUser.getId());
		//	coreUserData.setEmail(email);
		coreUserData.setFullName(coreUser.getFullName());
		coreUserData.setUsername(coreUser.getUsername());
//		List<CoreUser2Role> coreUser2Roles = coreUser2RoleService.findByUserNameAndDaXoaFalse(email);
//		Set<String> roles = coreUser2Roles.stream().map(CoreUser2Role::getRole).collect(Collectors.toSet());
//		String defaultRole = "ROLE_" + coreUser.getAppCode() + "_USER";
//		roles.add(defaultRole.toUpperCase());
//		List<CoreUserRole> coreUserRoles = coreUserRoleService.findByUserNameAndDaXoaFalse(coreUser.getUsername());
//		Set<String> roles = coreUserRoles.stream().map(CoreUserRole::getRoleCode).collect(Collectors.toSet());
//		coreUserData.setRoles(roles);
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
	
	@Transactional(readOnly = true)
	public Page<CoreUserData> findAll(CoreUserSearchCriteria criteria) {
		Pageable pageable = CoreUtils.getPageRequest(criteria.getPage(), criteria.getSize(), criteria.getSortBy(), criteria.getSortDir());
		Page<CoreUser> pageCoreUser = coreUserService.findAll(criteria, pageable);
		return pageCoreUser.map(coreUserMapper::toData);
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
		Optional<CoreUser> optionalCoreUser = coreUserService.findFirstByUsernameIgnoreCase(coreUserChangePasswordData.getUsername());
		if (optionalCoreUser.isEmpty()) {
			throw new EntityNotFoundException(CoreUser.class, coreUserChangePasswordData.getUsername());
		}
		CoreUser coreUser = optionalCoreUser.get();
		coreUser.setHashedPassword(BCrypt.hashpw(coreUserChangePasswordData.getPassword()));
		coreUserService.save(coreUser);
	}
	
	public void changeIsEnabled(CoreUserChangeIsEnabledData coreUserChangeIsEnabledData) {
		Optional<CoreUser> optionalCoreUser = coreUserService.findFirstByUsernameIgnoreCase(coreUserChangeIsEnabledData.getUsername());
		if (optionalCoreUser.isEmpty()) {
			throw new EntityNotFoundException(CoreUser.class, coreUserChangeIsEnabledData.getUsername());
		}
		CoreUser coreUser = optionalCoreUser.get();
		//coreUser.setStatus(Boolean.TRUE.equals(coreUserChangeIsEnabledData.getIsEnabled()));
		coreUserService.save(coreUser);
	}
	
	private void syncUserRelations(String username, CoreUserData userData) {
		coreUserAppService.replaceUserApps(username, userData.getApps());
		
		// Giả sử DTO.getRoles() trả về một Set<String> dạng "ROLE_SYT_ADMIN", "ROLE_SKHCN_USER"
		if (userData.getRoles() != null) {
			// Cần một hàm helper để chuyển Set<String> thành Map<String, Set<String>>
			Map<String, Set<String>> rolesByApp = groupRolesByApp(userData.getRoles());
			coreUserRoleService.replaceUserRoles(username, rolesByApp);
		}
		
		coreUserGroupService.replaceUserGroups(username, userData.getGroups());
	}
	
	private Map<String, Set<String>> groupRolesByApp(Set<String> flatRoleCodes) {
		if (flatRoleCodes == null) return Collections.emptyMap();
		return flatRoleCodes.stream()
				.filter(roleCode -> roleCode.startsWith("ROLE_") && roleCode.chars().filter(ch -> ch == '_').count() >= 2)
				.collect(Collectors.groupingBy(roleCode -> roleCode.split("_")[1].toLowerCase(), Collectors.toSet()));
	}
	
	private void syncUserContacts(String username, List<CoreContactData> newContactDataList) {
		final String ownerType = "CORE_USER";
		List<CoreContact> existingContacts = coreContactService.findByOwner(ownerType, username);
		Map<Long, CoreContact> existingContactsMap = existingContacts.stream()
				.collect(Collectors.toMap(CoreContact::getId, Function.identity()));
		
		if (newContactDataList == null) {
			newContactDataList = Collections.emptyList();
		}
		
		Map<Long, CoreContactData> newContactsMap = newContactDataList.stream()
				.filter(dto -> dto.getId() != null)
				.collect(Collectors.toMap(CoreContactData::getId, Function.identity()));
		
		List<CoreContact> toSave = new ArrayList<>();
		
		// Update hoặc Deactivate các contact cũ
		for (CoreContact existingContact : existingContacts) {
			CoreContactData dto = newContactsMap.get(existingContact.getId());
			if (dto != null) { // Update
				coreContactMapper.updateEntity(dto, existingContact);
				existingContact.setStatus("ACTIVE");
				toSave.add(existingContact);
			} else { // Deactivate
				existingContact.setStatus("INACTIVE");
				toSave.add(existingContact);
			}
		}
		
		// Insert các contact mới
		newContactDataList.stream()
				.filter(dto -> dto.getId() == null)
				.map(coreContactMapper::toEntity)
				.peek(contact -> {
					contact.setOwnerValue(username);
					contact.setOwnerType(ownerType);
					contact.setStatus("ACTIVE");
				})
				.forEach(toSave::add);
		
		if (!toSave.isEmpty()) {
			coreContactService.saveAll(toSave);
		}
		
		ensurePrimaryEmailContactExists(username, ownerType);
	}
	
	private void ensurePrimaryEmailContactExists(String username, String ownerType) {
		CoreUser user = coreUserService.findFirstByUsernameIgnoreCase(username).get();
		coreContactService.findByOwner(ownerType, username).stream()
				.filter(c -> "EMAIL".equals(c.getContactType()) && Boolean.TRUE.equals(c.getIsPrimary()))
				.findFirst()
				.ifPresentOrElse(
						primaryContact -> { // Nếu đã có primary email contact
							if (!primaryContact.getValue().equals(user.getEmail())) {
								primaryContact.setValue(user.getEmail());
								coreContactService.save(primaryContact);
							}
						},
						() -> { // Nếu chưa có
							CoreContact newPrimaryContact = new CoreContact();
							newPrimaryContact.setOwnerValue(username);
							newPrimaryContact.setOwnerType(ownerType);
							newPrimaryContact.setContactType("EMAIL");
							newPrimaryContact.setValue(user.getEmail());
							newPrimaryContact.setIsPrimary(true);
							newPrimaryContact.setStatus("ACTIVE");
							coreContactService.save(newPrimaryContact);
						}
				                );
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
	
}
