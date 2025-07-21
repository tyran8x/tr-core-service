package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.jpa.helper.AssociationSyncHelper;
import vn.tr.core.dao.model.CoreUserGroup;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreUserGroupServiceImpl implements CoreUserGroupService {
	
	private final CoreUserGroupRepo coreUserGroupRepo;
	private final AssociationSyncHelper associationSyncHelper;
	
	@Override
	public CoreUserGroup save(CoreUserGroup coreUser2Group) {
		return coreUserGroupRepo.save(coreUser2Group);
	}
	
	@Override
	public void deleteById(Long id) {
		coreUserGroupRepo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return coreUserGroupRepo.existsById(id);
	}
	
	@Override
	public Optional<CoreUserGroup> findById(Long id) {
		return coreUserGroupRepo.findById(id);
	}
	
	@Override
	@Transactional
	public void synchronizeUserGroupsInApp(String username, String appCode, Set<String> newGroupCodes) {
		log.info("Bắt đầu đồng bộ hóa các group cho người dùng '{}' trong ứng dụng '{}'", username, appCode);
		
		var ownerContext = new CoreUserGroupContext(username, appCode);
		
		List<CoreUserGroup> existingAssignments = coreUserGroupRepo.findAllByUsernameAndAppCodeIncludingDeleted(
				ownerContext.username(), ownerContext.appCode());
		
		associationSyncHelper.synchronize(
				ownerContext,
				existingAssignments,
				newGroupCodes,
				CoreUserGroup::getGroupCode,
				CoreUserGroup::new,
				(association, context) -> {
					association.setUsername(context.username());
					association.setAppCode(context.appCode());
				},
				CoreUserGroup::setGroupCode,
				coreUserGroupRepo);
	}
	
	@Override
	@Transactional
	public void assignUserToGroupInApp(String username, String appCode, String groupCode) {
		Set<String> currentGroups = this.findActiveGroupCodesByUsernameAndAppCode(username, appCode);
		
		if (!currentGroups.contains(groupCode)) {
			currentGroups.add(groupCode);
			synchronizeUserGroupsInApp(username, appCode, currentGroups);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public Set<String> findActiveGroupCodesByUsernameAndAppCode(String username, String appCode) {
		if (username.isBlank() || appCode.isBlank()) {
			return Collections.emptySet();
		}
		return coreUserGroupRepo.findActiveGroupCodesByUsernameAndAppCode(username.toLowerCase(), appCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Set<String> findAllActiveGroupCodesByUsername(String username) {
		if (username.isBlank()) {
			return Collections.emptySet();
		}
		return coreUserGroupRepo.findAllActiveGroupCodesByUsername(username.toLowerCase());
	}
	
	private record CoreUserGroupContext(String username, String appCode) {
	}
	
}
