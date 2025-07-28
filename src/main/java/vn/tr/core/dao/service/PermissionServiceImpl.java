package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.service.PermissionService;
import vn.tr.core.dao.model.CoreUser;

import java.util.Collections;
import java.util.Set;

/**
 * Lớp triển khai thực tế cho PermissionService, có hỗ trợ kiến trúc đa ứng dụng.
 *
 * @author tyran8x
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionServiceImpl implements PermissionService {
	
	private final CoreUserService coreUserService;
	private final CoreUserRoleService coreUserRoleService;
	private final CoreUserGroupService coreUserGroupService;
	private final CoreRolePermissionService coreRolePermissionService;
	
	/**
	 * Lấy danh sách các mã nhóm của một người dùng TRONG một ứng dụng cụ thể.
	 */
	@Override
	@Transactional(readOnly = true)
	public Set<String> getGroupCodes(Long userId, String appCode) {
		log.debug("DB Fallback: Đang lấy nhóm cho userId {} trong app {}", userId, appCode);
		return coreUserService.findById(userId)
				.map(CoreUser::getUsername)
				.map(username -> coreUserGroupService.findActiveGroupCodesByUsernameAndAppCode(username, appCode))
				.orElse(Collections.emptySet());
	}
	
	/**
	 * Lấy danh sách các mã quyền hạn của một người dùng TRONG một ứng dụng cụ thể.
	 */
	@Override
	@Transactional(readOnly = true)
	public Set<String> getPermissionCodes(Long userId, String appCode) {
		log.debug("DB Fallback: Đang lấy quyền hạn cho userId {} trong app {}", userId, appCode);
		
		// Bước 1: Lấy các vai trò của người dùng trong ứng dụng đó
		Set<String> roleCodes = this.getRoleCodes(userId, appCode);
		
		if (roleCodes.isEmpty()) {
			return Collections.emptySet();
		}
		
		// Bước 2: Lấy tất cả các quyền từ các vai trò đó, cũng trong phạm vi ứng dụng
		// Cần phương thức findPermissionCodesByRoleCodesAndAppCode
		return coreRolePermissionService.findPermissionCodesByRoleCodesAndAppCode(roleCodes, appCode);
	}
	
	/**
	 * Lấy danh sách các mã vai trò của một người dùng TRONG một ứng dụng cụ thể.
	 */
	@Override
	@Transactional(readOnly = true)
	public Set<String> getRoleCodes(Long userId, String appCode) {
		log.debug("DB Fallback: Đang lấy vai trò cho userId {} trong app {}", userId, appCode);
		return coreUserService.findById(userId)
				.map(CoreUser::getUsername)
				.map(username -> coreUserRoleService.findActiveRoleCodesByUsernameAndAppCode(username, appCode))
				.orElse(Collections.emptySet());
	}
}
