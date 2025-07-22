package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.jpa.helper.AssociationSyncHelper;
import vn.tr.common.jpa.helper.GenericUpsertHelper;
import vn.tr.core.dao.model.CoreApp;
import vn.tr.core.dao.model.CoreRole;
import vn.tr.core.dao.model.CoreRolePermission;
import vn.tr.core.data.criteria.CoreRoleSearchCriteria;
import vn.tr.core.data.dto.CoreRoleData;
import vn.tr.core.data.mapper.CoreRoleMapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreRoleServiceImpl implements CoreRoleService {
	
	private final CoreRoleRepo coreRoleRepo;
	private final CoreRolePermissionRepo coreRolePermissionRepo;
	private final CoreRoleMapper coreRoleMapper;
	private final GenericUpsertHelper genericUpsertHelper;
	private final AssociationSyncHelper associationSyncHelper;
	
	@Override
	public Optional<CoreRole> findById(Long id) {
		return coreRoleRepo.findById(id);
	}
	
	@Override
	public CoreRole save(CoreRole coreRole) {
		return coreRoleRepo.save(coreRole);
	}
	
	@Override
	public void delete(Long id) {
		coreRoleRepo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return coreRoleRepo.existsById(id);
	}
	
	@Override
	public Page<CoreRole> findAll(CoreRoleSearchCriteria coreRoleSearchCriteria, Pageable pageable) {
		return coreRoleRepo.findAll(CoreRoleSpecifications.quickSearch(coreRoleSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreRole> findAll(CoreRoleSearchCriteria coreRoleSearchCriteria) {
		return coreRoleRepo.findAll(CoreRoleSpecifications.quickSearch(coreRoleSearchCriteria));
	}
	
	@Override
	public boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode) {
		return coreRoleRepo.existsByIdNotAndCodeIgnoreCaseAndAppCode(id, code, appCode);
	}
	
	@Override
	public boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode) {
		return coreRoleRepo.existsByIdNotAndNameIgnoreCaseAndAppCode(id, name, appCode);
	}
	
	@Override
	public boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode) {
		return coreRoleRepo.existsByCodeIgnoreCaseAndAppCode(code, appCode);
	}
	
	@Override
	public boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode) {
		return coreRoleRepo.existsByNameIgnoreCaseAndAppCode(name, appCode);
	}
	
	@Override
	public boolean existsByIdAndAppCode(long id, String appCode) {
		return coreRoleRepo.existsByIdAndAppCode(id, appCode);
	}
	
	@Override
	public boolean existsByCodeIgnoreCase(String code) {
		return coreRoleRepo.existsByCodeIgnoreCase(code);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Set<Long> ids) {
		if (ids.isEmpty()) {
			return;
		}
		coreRoleRepo.softDeleteByIds(ids);
	}
	
	@Override
	@Transactional
	public CoreRole findOrCreate(CoreApp coreApp, String roleCode, String roleName) {
		log.debug("Đang tìm hoặc tạo vai trò: code='{}', name='{}' trong app='{}'",
				roleCode, roleName, coreApp.getCode());
		
		// 1. Đóng gói dữ liệu đầu vào
		var seedData = new CoreRoleSeedData(coreApp.getCode(), roleCode, roleName);
		
		// 2. Gọi Upsert Helper
		return genericUpsertHelper.upsert(
				seedData,
				// Hàm tìm kiếm: Luôn tìm cả bản ghi đã xóa mềm
				() -> coreRoleRepo.findByCodeAndAppCodeEvenIfDeleted(seedData.roleCode(), seedData.appCode()),
				
				// Hàm tạo mới: Chỉ được gọi nếu không tìm thấy
				() -> {
					CoreRole newRole = new CoreRole();
					newRole.setAppCode(seedData.appCode());
					newRole.setCode(seedData.roleCode());
					// Các giá trị mặc định khác có thể được set ở đây
					return newRole;
				},
				
				// Hàm cập nhật: Sẽ được gọi trong cả hai trường hợp (tìm thấy hoặc tạo mới)
				// Trong trường hợp này, chúng ta chỉ muốn cập nhật/gán 'name'.
				(data, entity) -> {
					// Chỉ cập nhật 'name' nếu nó chưa có hoặc khác với giá trị mới.
					// Điều này tránh ghi đè tên đã được admin tùy chỉnh.
					if (entity.getName() == null || entity.getName().isBlank()) {
						entity.setName(data.roleName());
					}
				},
				
				coreRoleRepo
		                                 );
	}
	
	@Override
	@Transactional
	public CoreRoleData upsert(CoreRoleData roleData) {
		log.info("Bắt đầu xử lý Upsert cho vai trò: code='{}', app='{}'", roleData.getCode(), roleData.getAppCode());
		
		// 1. Dùng Upsert Helper để xử lý CoreRole
		CoreRole savedRole = genericUpsertHelper.upsert(
				roleData,
				() -> coreRoleRepo.findByCodeAndAppCodeEvenIfDeleted(roleData.getCode(), roleData.getAppCode()),
				() -> {
					CoreRole newRole = new CoreRole();
					newRole.setCode(roleData.getCode());
					newRole.setAppCode(roleData.getAppCode());
					return newRole;
				},
				coreRoleMapper::updateEntityFromData,
				coreRoleRepo);
		
		// 2. Dùng Association Sync Helper để đồng bộ hóa permissions
		if (roleData.getPermissionCodes() != null) {
			synchronizePermissionsForRole(savedRole, roleData.getPermissionCodes());
		}
		
		return coreRoleMapper.toData(savedRole);
	}
	
	private void synchronizePermissionsForRole(CoreRole role, Set<String> newPermissionCodes) {
		var ownerContext = new CoreRoleContext(role.getCode(), role.getAppCode());
		
		List<CoreRolePermission> existingPermissions = coreRolePermissionRepo.findAllByRoleCodeAndAppCodeEvenIfDeleted(
				ownerContext.roleCode(), ownerContext.appCode()
		                                                                                                              );
		
		associationSyncHelper.synchronize(
				ownerContext,
				existingPermissions,
				newPermissionCodes,
				CoreRolePermission::getPermissionCode,
				CoreRolePermission::new,
				(association, context) -> {
					association.setRoleCode(context.roleCode());
					association.setAppCode(context.appCode());
				},
				CoreRolePermission::setPermissionCode,
				coreRolePermissionRepo);
	}
	
	private record CoreRoleSeedData(String appCode, String roleCode, String roleName) {
	}
	
	private record CoreRoleContext(String roleCode, String appCode) {
	}
	
}
