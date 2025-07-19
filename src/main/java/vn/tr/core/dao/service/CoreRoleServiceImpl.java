package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.core.dao.model.CoreApp;
import vn.tr.core.dao.model.CoreRole;
import vn.tr.core.dao.model.CoreRolePermission;
import vn.tr.core.data.criteria.CoreRoleSearchCriteria;
import vn.tr.core.data.dto.CoreRoleData;
import vn.tr.core.data.mapper.CoreRoleMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreRoleServiceImpl implements CoreRoleService {
	
	private final CoreRoleRepo repo;
	private final CoreRolePermissionRepo coreRolePermissionRepo;
	private final CoreRoleMapper coreRoleMapper;
	
	@Override
	public Optional<CoreRole> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public CoreRole save(CoreRole coreRole) {
		return repo.save(coreRole);
	}
	
	@Override
	public void delete(Long id) {
		repo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}
	
	@Override
	public Page<CoreRole> findAll(CoreRoleSearchCriteria coreRoleSearchCriteria, Pageable pageable) {
		return repo.findAll(CoreRoleSpecifications.quickSearch(coreRoleSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreRole> findAll(CoreRoleSearchCriteria coreRoleSearchCriteria) {
		return repo.findAll(CoreRoleSpecifications.quickSearch(coreRoleSearchCriteria));
	}
	
	@Override
	public boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode) {
		return repo.existsByIdNotAndCodeIgnoreCaseAndAppCode(id, code, appCode);
	}
	
	@Override
	public boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode) {
		return repo.existsByIdNotAndNameIgnoreCaseAndAppCode(id, name, appCode);
	}
	
	@Override
	public boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode) {
		return repo.existsByCodeIgnoreCaseAndAppCode(code, appCode);
	}
	
	@Override
	public boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode) {
		return repo.existsByNameIgnoreCaseAndAppCode(name, appCode);
	}
	
	@Override
	public boolean existsByIdAndAppCode(long id, String appCode) {
		return repo.existsByIdAndAppCode(id, appCode);
	}
	
	@Override
	public boolean existsByCodeIgnoreCase(String code) {
		return repo.existsByCodeIgnoreCase(code);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Set<Long> ids) {
		if (ids.isEmpty()) {
			return;
		}
		repo.softDeleteByIds(ids);
	}
	
	@Override
	@Transactional
	public CoreRole findOrCreate(CoreApp coreApp, String roleCode, String roleName) {
		return repo.findFirstByAppCodeAndCodeIgnoreCase(coreApp.getCode(), roleCode)
				.orElseGet(() -> {
					CoreRole newRole = new CoreRole();
					newRole.setAppCode(coreApp.getCode());
					newRole.setCode(roleCode);
					newRole.setName(roleName);
					return repo.save(newRole);
				});
	}
	
	@Transactional
	public CoreRoleData createOrUpdateRole(CoreRoleData roleData) {
		log.info("Bắt đầu xử lý vai trò với mã: {} trong app: {}", roleData.getCode(), roleData.getAppCode());
		
		// 1. Tìm vai trò (kể cả đã xóa mềm), sau đó quyết định cập nhật hay tạo mới.
		CoreRole roleEntity = repo.findByCodeAndAppCodeEvenIfDeleted(roleData.getCode(), roleData.getAppCode())
				.map(existingRole -> {
					// TRƯỜNG HỢP 1: TÌM THẤY VAI TRÒ (Dù đang hoạt động hay đã bị xóa mềm)
					log.info("Tìm thấy vai trò đã tồn tại (ID: {}). Sẽ cập nhật và kích hoạt lại.", existingRole.getId());
					
					// Kích hoạt lại vai trò nếu nó đã bị xóa mềm
					if (existingRole.getDeletedAt() != null) {
						existingRole.setDeletedAt(null);
					}
					// Đảm bảo trạng thái là ACTIVE khi cập nhật
					existingRole.setStatus(LifecycleStatus.ACTIVE);
					
					// Áp dụng các thay đổi từ DTO
					coreRoleMapper.updateEntityFromData(roleData, existingRole);
					return existingRole;
				})
				.orElseGet(() -> {
					// TRƯỜNG HỢP 2: KHÔNG TÌM THẤY -> TẠO MỚI HOÀN TOÀN
					log.info("Không tìm thấy vai trò, tiến hành tạo mới.");
					CoreRole newRole = new CoreRole();
					newRole.setAppCode(roleData.getAppCode()); // Bắt buộc gán appCode khi tạo mới
					// Áp dụng các thay đổi từ DTO
					coreRoleMapper.updateEntityFromData(roleData, newRole);
					return newRole;
				});
		
		// 2. Lưu thông tin cơ bản của vai trò để có ID (nếu là vai trò mới)
		CoreRole savedRole = repo.save(roleEntity);
		log.info("Đã lưu thông tin cơ bản cho vai trò ID: {}", savedRole.getId());
		
		// 3. Đồng bộ hóa danh sách quyền (logic này không đổi)
		synchronizePermissionsForRole(savedRole, roleData.getPermissionCodes());
		
		// 4. Trả về DTO
		return coreRoleMapper.toData(savedRole);
	}
	
	private void synchronizePermissionsForRole(CoreRole role, List<String> newPermissionCodes) {
		String roleCode = role.getCode();
		String appCode = role.getAppCode();
		log.info("Bắt đầu đồng bộ hóa quyền cho vai trò '{}' trong app '{}'", roleCode, appCode);
		
		// 1. Lấy trạng thái hiện tại từ DB (bao gồm cả đã xóa mềm)
		List<CoreRolePermission> existingPermissions = coreRolePermissionRepo.findAllByRoleCodeAndAppCodeEvenIfDeleted(roleCode, appCode);
		Map<String, CoreRolePermission> existingMap = existingPermissions.stream()
				.collect(Collectors.toMap(CoreRolePermission::getPermissionCode, Function.identity()));
		
		// 2. Chuyển danh sách mới thành Set để tối ưu việc tìm kiếm (O(1))
		Set<String> newCodeSet = new HashSet<>(newPermissionCodes);
		
		List<CoreRolePermission> permissionsToSave = new ArrayList<>();
		
		// 3. Xử lý các quyền đang có trong DB
		for (CoreRolePermission existingPerm : existingPermissions) {
			boolean shouldBeActive = newCodeSet.contains(existingPerm.getPermissionCode());
			
			if (!shouldBeActive && existingPerm.getDeletedAt() == null) {
				log.debug("Xóa mềm quyền: {}", existingPerm.getPermissionCode());
				existingPerm.setDeletedAt(LocalDateTime.now());
				permissionsToSave.add(existingPerm);
			} else if (shouldBeActive && existingPerm.getDeletedAt() != null) {
				log.debug("Kích hoạt lại quyền: {}", existingPerm.getPermissionCode());
				existingPerm.setDeletedAt(null);
				permissionsToSave.add(existingPerm);
			}
		}
		
		// 4. Xử lý các quyền mới cần thêm
		for (String newPermCode : newCodeSet) {
			if (!existingMap.containsKey(newPermCode)) {
				log.debug("Thêm mới quyền: {}", newPermCode);
				CoreRolePermission newAssociation = new CoreRolePermission();
				newAssociation.setRoleCode(roleCode);
				newAssociation.setPermissionCode(newPermCode);
				newAssociation.setAppCode(appCode);
				permissionsToSave.add(newAssociation);
			}
		}
		
		// 5. Lưu tất cả thay đổi vào DB
		if (!permissionsToSave.isEmpty()) {
			log.info("Lưu {} thay đổi về quyền.", permissionsToSave.size());
			coreRolePermissionRepo.saveAll(permissionsToSave);
		} else {
			log.info("Không có thay đổi nào về quyền cần lưu.");
		}
	}
	
}
