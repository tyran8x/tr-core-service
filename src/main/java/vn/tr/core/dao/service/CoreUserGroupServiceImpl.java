package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vn.tr.core.dao.model.CoreGroup;
import vn.tr.core.dao.model.CoreUserGroup;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Lớp triển khai cho CoreUserGroupService. Chỉ chứa các logic truy vấn và CRUD cơ bản, không chứa nghiệp vụ phức tạp.
 *
 * @author tyran8x
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CoreUserGroupServiceImpl implements CoreUserGroupService {
	
	private final CoreUserGroupRepo coreUserGroupRepo;
	
	@Override
	public List<CoreUserGroup> findByUsernameAndAppCodeIncludingDeleted(String username, String appCode) {
		return coreUserGroupRepo.findAllByUsernameAndAppCodeIncludingDeleted(username, appCode);
	}
	
	@Override
	public CoreUserGroup save(CoreUserGroup coreUserGroup) {
		return coreUserGroupRepo.save(coreUserGroup);
	}
	
	@Override
	public void deleteById(Long id) {
		coreUserGroupRepo.deleteById(id);
	}
	
	@Override
	public JpaRepository<CoreUserGroup, Long> getRepository() {
		return this.coreUserGroupRepo;
	}
	
	@Override
	public List<CoreUserGroup> findByUsernameAndAppCode(String username, String appCode) {
		return coreUserGroupRepo.findByUsernameAndAppCode(username, appCode);
	}
	
	@Override
	public Set<String> findActiveGroupCodesByUsernameAndAppCode(String username, String appCode) {
		if (username.isBlank() || appCode.isBlank()) {
			return Collections.emptySet();
		}
		return coreUserGroupRepo.findActiveGroupCodesByUsernameAndAppCode(username, appCode);
	}
	
	@Override
	public Set<String> findAllActiveGroupCodesByUsername(String username) {
		if (username.isBlank()) {
			return Collections.emptySet();
		}
		return coreUserGroupRepo.findAllActiveGroupCodesByUsername(username);
	}
	
	@Override
	public Map<String, Set<String>> findActiveGroupCodesForUsersInApp(Collection<String> usernames, String appCode) {
		if (usernames.isEmpty()) {
			return Collections.emptyMap();
		}
		List<CoreUserGroup> assignments = coreUserGroupRepo.findActiveForUsersInApp(usernames, appCode);
		return assignments.stream()
				.collect(Collectors.groupingBy(
						CoreUserGroup::getUsername,
						Collectors.mapping(CoreUserGroup::getGroupCode, Collectors.toSet())
				                              ));
	}
	
	/**
	 * Kiểm tra xem một CoreGroup cụ thể có đang được sử dụng hay không. Logic này dựa trên việc kiểm tra sự tồn tại của bản ghi trong core_user_group
	 * bằng cách sử dụng group_code và app_code từ đối tượng CoreGroup.
	 *
	 * @param group Đối tượng CoreGroup cần kiểm tra.
	 *
	 * @return true nếu có ít nhất một user được gán vào group này.
	 */
	@Override
	public boolean isGroupInUse(CoreGroup group) {
		if (group.getCode() == null || group.getAppCode() == null) {
			// Nếu không có đủ thông tin để kiểm tra, coi như không được sử dụng để tránh lỗi.
			return false;
		}
		return coreUserGroupRepo.existsByGroupCodeAndAppCode(group.getCode(), group.getAppCode());
	}
	
	@Override
	public Map<String, Set<String>> findAllActiveGroupCodesForUsers(Collection<String> usernames) {
		if (usernames.isEmpty()) {
			return Collections.emptyMap();
		}
		List<CoreUserGroup> assignments = coreUserGroupRepo.findActiveByUsernames(usernames);
		return assignments.stream()
				.collect(Collectors.groupingBy(
						CoreUserGroup::getUsername,
						Collectors.mapping(CoreUserGroup::getGroupCode, Collectors.toSet())));
	}
}
