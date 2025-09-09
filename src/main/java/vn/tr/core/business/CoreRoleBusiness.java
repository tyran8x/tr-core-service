package vn.tr.core.business;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.exception.base.DataConstraintViolationException;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.core.exception.base.PermissionDeniedException;
import vn.tr.common.jpa.helper.AssociationSyncHelper;
import vn.tr.common.jpa.helper.GenericUpsertHelper;
import vn.tr.common.web.data.dto.BulkOperationResult;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.common.web.utils.PagedResult;
import vn.tr.core.dao.model.CorePermission;
import vn.tr.core.dao.model.CoreRole;
import vn.tr.core.dao.model.CoreRolePermission;
import vn.tr.core.dao.service.CorePermissionService;
import vn.tr.core.dao.service.CoreRolePermissionService;
import vn.tr.core.dao.service.CoreRoleService;
import vn.tr.core.dao.service.CoreUserRoleService;
import vn.tr.core.data.criteria.CoreRoleSearchCriteria;
import vn.tr.core.data.dto.CoreRoleData;
import vn.tr.core.data.mapper.CoreRoleMapper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Lớp Business (Facade) điều phối các nghiệp vụ phức tạp liên quan đến Quản lý Vai trò (CoreRole).
 * Mọi thao tác đều được thực hiện trong ngữ cảnh của một ứng dụng (appCodeContext).
 *
 * @author tyran8x
 * @version 2.1
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CoreRoleBusiness {
	
	private final CoreRoleService coreRoleService;
	private final CoreUserRoleService coreUserRoleService;
	private final CoreRolePermissionService coreRolePermissionService;
	private final CorePermissionService corePermissionService;
	private final CoreRoleMapper coreRoleMapper;
	private final GenericUpsertHelper genericUpsertHelper;
	private final AssociationSyncHelper associationSyncHelper;
	
	/**
	 * Tạo mới một vai trò trong ngữ cảnh của một ứng dụng.
	 *
	 * @param data           DTO chứa thông tin vai trò.
	 * @param appCodeContext Mã của ứng dụng mà vai trò thuộc về.
	 *
	 * @return Dữ liệu của vai trò sau khi tạo.
	 */
	public CoreRoleData create(CoreRoleData data, String appCodeContext) {
		data.setAppCode(appCodeContext);
		return upsertByCode(data, appCodeContext);
	}
	
	private CoreRoleData upsertByCode(CoreRoleData coreRoleData, String appCodeContext) {
		CoreRole role = genericUpsertHelper.upsert(
				coreRoleData,
				() -> coreRoleService.findByCodeAndAppCodeIncludingDeleted(coreRoleData.getCode(), appCodeContext),
				() -> coreRoleMapper.toEntity(coreRoleData),
				coreRoleMapper::updateEntityFromData,
				coreRoleService.getRepository());
		role.setAppCode(appCodeContext);
		CoreRole savedRole = coreRoleService.save(role);
		
		synchronizePermissionsForRole(savedRole.getId(), coreRoleData.getPermissionCodes(), appCodeContext);
		return coreRoleMapper.toData(savedRole);
	}
	
	/**
	 * Đồng bộ hóa (thêm/xóa) các quyền hạn cho một vai trò.
	 * Phiên bản này đã được cập nhật để tương thích với chữ ký của AssociationSyncHelper.
	 *
	 * @param roleId          ID của vai trò cần đồng bộ.
	 * @param permissionCodes Set các mã quyền hạn mới.
	 * @param appCodeContext  Ngữ cảnh ứng dụng để kiểm tra quyền.
	 *
	 * @return Dữ liệu của vai trò sau khi cập nhật.
	 */
	@Transactional
	public CoreRoleData synchronizePermissionsForRole(Long roleId, Set<String> permissionCodes, String appCodeContext) {
		// 1. Tìm vai trò và kiểm tra quyền
		CoreRole role = coreRoleService.findById(roleId)
				.orElseThrow(() -> new EntityNotFoundException(CoreRole.class, roleId));
		if (!role.getAppCode().equals(appCodeContext)) {
			throw new PermissionDeniedException("Không có quyền thay đổi quyền hạn cho vai trò này.");
		}
		
		// 2. Định nghĩa ngữ cảnh (Owner Context)
		record RolePermissionSyncContext(String roleCode, String appCode) {
		}
		var ownerContext = new RolePermissionSyncContext(role.getCode(), appCodeContext);
		
		// 3. Chuẩn bị danh sách các khóa mới (newKeys)
		// Lấy danh sách đầy đủ các thực thể Permission để đảm bảo chúng tồn tại
		List<CorePermission> validNewPermissions = corePermissionService.findAllByAppCodeAndCodeIn(appCodeContext, permissionCodes);
		Set<String> validNewPermissionCodes = validNewPermissions.stream()
				.map(CorePermission::getCode)
				.collect(Collectors.toSet());
		
		// 4. Gọi helper với đúng 8 tham số
		associationSyncHelper.synchronize(
				// Tham số 1: ownerContext - Ngữ cảnh của "chủ thể"
				ownerContext,
				
				// Tham số 2: existingAssociations - Danh sách liên kết cũ (bao gồm cả đã xóa mềm)
				coreRolePermissionService.findByRoleCodeAndAppCodeIncludingDeleted(role.getCode(), appCodeContext),
				
				// Tham số 3: newKeys - Tập hợp các khóa mới (permission_code)
				validNewPermissionCodes,
				
				// Tham số 4: keyExtractor - Hàm để lấy khóa từ bản ghi liên kết cũ
				CoreRolePermission::getPermissionCode,
				
				// Tham số 5: associationFactory - Hàm để tạo một bản ghi liên kết mới (rỗng)
				CoreRolePermission::new,
				
				// Tham số 6: ownerContextSetter - Hàm để gán thông tin từ "chủ thể" vào bản ghi mới
				(association, context) -> {
					association.setRoleCode(context.roleCode());
					association.setAppCode(context.appCode());
				},
				
				// Tham số 7: keySetter - Hàm để gán "khóa" (permission_code) vào bản ghi mới
				CoreRolePermission::setPermissionCode,
				
				// Tham số 8: repository - Repository để helper tự lưu và xóa
				coreRolePermissionService.getRepository()
		                                 );
		
		// 5. Cân nhắc làm mới cache ở đây nếu có
		// corePermissionCacheService.refreshRolePermissions(role.getCode());
		
		// 6. Trả về thông tin vai trò đã được cập nhật
		return findById(roleId, appCodeContext);
	}
	
	/**
	 * Tìm và trả về thông tin chi tiết của một vai trò.
	 *
	 * @param id             ID của vai trò cần tìm.
	 * @param appCodeContext Ngữ cảnh ứng dụng để kiểm tra quyền.
	 *
	 * @return Dữ liệu chi tiết của vai trò.
	 */
	@Transactional(readOnly = true)
	public CoreRoleData findById(Long id, String appCodeContext) {
		CoreRole role = coreRoleService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreRole.class, id));
		if (!role.getAppCode().equals(appCodeContext)) {
			throw new PermissionDeniedException("Không có quyền xem vai trò thuộc ứng dụng khác.");
		}
		return mapEntityToDataWithRelations(role);
	}
	
	private CoreRoleData mapEntityToDataWithRelations(CoreRole role) {
		// 1. Ánh xạ các trường cơ bản
		CoreRoleData data = coreRoleMapper.toData(role);
		
		// 2. Lấy và gán danh sách quyền hạn
		Set<String> permissionCodes = coreRolePermissionService.findPermissionCodesByRoleCodesAndAppCode(
				Collections.singletonList(role.getCode()), role.getAppCode());
		data.setPermissionCodes(permissionCodes);
		
		return data;
	}
	
	/**
	 * Cập nhật một vai trò trong ngữ cảnh của một ứng dụng.
	 *
	 * @param id             ID của vai trò cần cập nhật.
	 * @param data           DTO chứa thông tin mới.
	 * @param appCodeContext Mã của ứng dụng (để xác thực quyền).
	 *
	 * @return Dữ liệu của vai trò sau khi cập nhật.
	 */
	public CoreRoleData update(Long id, CoreRoleData data, String appCodeContext) {
		CoreRole existing = coreRoleService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreRole.class, id));
		if (!existing.getAppCode().equals(appCodeContext)) {
			throw new PermissionDeniedException("Không có quyền cập nhật vai trò thuộc ứng dụng khác.");
		}
		data.setAppCode(appCodeContext);
		return upsertByCode(data, appCodeContext);
	}
	
	/**
	 * Xóa một vai trò duy nhất (xóa mềm).
	 *
	 * @param id             ID của vai trò cần xóa.
	 * @param appCodeContext Ngữ cảnh ứng dụng để kiểm tra quyền.
	 */
	public void delete(Long id, String appCodeContext) {
		CoreRole role = coreRoleService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreRole.class, id));
		validateDeletable(role, appCodeContext);
		coreRoleService.deleteByIds(List.of(id));
	}
	
	private void validateDeletable(CoreRole role, String appCodeContext) {
		if (!role.getAppCode().equals(appCodeContext)) {
			throw new PermissionDeniedException(String.format("Không có quyền xóa vai trò '%s'.", role.getName()));
		}
		if (coreUserRoleService.isRoleInUse(role)) {
			throw new DataConstraintViolationException(String.format("Không thể xóa vai trò '%s' vì đang có người dùng được gán.", role.getName()));
		}
		if (coreRolePermissionService.isRoleInUse(role)) {
			throw new DataConstraintViolationException(
					String.format("Không thể xóa vai trò '%s' vì đang có quyền hạn được gán vào.", role.getName()));
		}
	}
	
	/**
	 * Xóa hàng loạt vai trò và trả về kết quả chi tiết cho từng item.
	 *
	 * @param ids            Collection các ID cần xóa.
	 * @param appCodeContext Ngữ cảnh ứng dụng để kiểm tra quyền.
	 *
	 * @return Một đối tượng chứa danh sách các ID xóa thành công và thất bại.
	 */
	public BulkOperationResult<Long> bulkDelete(Collection<Long> ids, String appCodeContext) {
		BulkOperationResult<Long> result = new BulkOperationResult<>();
		if (ids == null || ids.isEmpty()) return result;
		
		List<CoreRole> rolesInDb = coreRoleService.findAllByIds(ids);
		Map<Long, CoreRole> roleMap = rolesInDb.stream().collect(Collectors.toMap(CoreRole::getId, r -> r));
		
		for (Long id : ids) {
			CoreRole role = roleMap.get(id);
			if (role == null) {
				result.addFailure(id, "Vai trò không tồn tại.");
				continue;
			}
			try {
				validateDeletable(role, appCodeContext);
				result.addSuccess(id);
			} catch (PermissionDeniedException | DataConstraintViolationException e) {
				result.addFailure(id, e.getMessage());
			} catch (Exception e) {
				log.error("Lỗi không xác định khi kiểm tra xóa vai trò ID {}: {}", id, e.getMessage());
				result.addFailure(id, "Lỗi hệ thống không xác định.");
			}
		}
		if (!result.getSuccessfulItems().isEmpty()) {
			coreRoleService.deleteByIds(result.getSuccessfulItems());
		}
		return result;
	}
	
	@Transactional(readOnly = true)
	public PagedResult<CoreRoleData> findAll(CoreRoleSearchCriteria criteria, String appCodeContext) {
		criteria.setAppCode(appCodeContext);
		Pageable pageable = CoreUtils.getPageRequest(criteria);
		Page<CoreRole> page = coreRoleService.findAll(criteria, pageable);
		Set<String> roleCodesInPage = page.getContent().stream().map(CoreRole::getCode).collect(Collectors.toSet());
		Map<String, Set<String>> permissionsMap = coreRolePermissionService.findActivePermissionsForRoles(roleCodesInPage, appCodeContext);
		return PagedResult.from(page, role -> mapEntityToDataWithRelations(role, permissionsMap.getOrDefault(role.getCode(), Set.of())));
	}
	
	private CoreRoleData mapEntityToDataWithRelations(CoreRole role, Set<String> permissions) {
		CoreRoleData data = coreRoleMapper.toData(role);
		data.setPermissionCodes(permissions);
		return data;
	}
}
