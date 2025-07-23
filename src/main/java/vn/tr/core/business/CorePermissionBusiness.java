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
import vn.tr.common.jpa.helper.GenericUpsertHelper;
import vn.tr.common.web.data.dto.BulkOperationResult;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.common.web.utils.PagedResult;
import vn.tr.core.dao.model.CorePermission;
import vn.tr.core.dao.service.CorePermissionService;
import vn.tr.core.dao.service.CoreRolePermissionService;
import vn.tr.core.data.criteria.CorePermissionSearchCriteria;
import vn.tr.core.data.dto.CorePermissionData;
import vn.tr.core.data.mapper.CorePermissionMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lớp Business (Facade) điều phối các nghiệp vụ phức tạp liên quan đến Quyền hạn (CorePermission).
 *
 * @author tyran8x
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CorePermissionBusiness {
	
	private final CorePermissionService corePermissionService;
	private final CoreRolePermissionService coreRolePermissionService;
	private final CorePermissionMapper corePermissionMapper;
	private final GenericUpsertHelper genericUpsertHelper;
	
	public CorePermissionData create(CorePermissionData data, String appCodeContext) {
		data.setAppCode(appCodeContext);
		return upsertByCode(data, appCodeContext);
	}
	
	private CorePermissionData upsertByCode(CorePermissionData data, String appCodeContext) {
		CorePermission permission = genericUpsertHelper.upsert(
				data,
				() -> corePermissionService.findByCodeAndAppCodeIncludingDeleted(data.getCode(), appCodeContext),
				() -> corePermissionMapper.toEntity(data),
				corePermissionMapper::updateEntityFromData,
				corePermissionService.getRepository()
		                                                      );
		permission.setAppCode(appCodeContext);
		// Cần logic để validate và gán moduleId ở đây
		CorePermission savedPermission = corePermissionService.save(permission);
		return corePermissionMapper.toData(savedPermission);
	}
	
	public CorePermissionData update(Long id, CorePermissionData data, String appCodeContext) {
		CorePermission existing = corePermissionService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CorePermission.class, id));
		if (!existing.getAppCode().equals(appCodeContext)) {
			throw new PermissionDeniedException("Không có quyền cập nhật quyền hạn thuộc ứng dụng khác.");
		}
		data.setAppCode(appCodeContext);
		return upsertByCode(data, appCodeContext);
	}
	
	public void delete(Long id, String appCodeContext) {
		CorePermission permission = corePermissionService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CorePermission.class, id));
		validateDeletable(permission, appCodeContext);
		corePermissionService.deleteByIds(List.of(id));
	}
	
	private void validateDeletable(CorePermission permission, String appCodeContext) {
		if (!permission.getAppCode().equals(appCodeContext)) {
			throw new PermissionDeniedException(String.format("Không có quyền xóa quyền hạn '%s'.", permission.getName()));
		}
		if (coreRolePermissionService.isPermissionInUse(permission)) {
			throw new DataConstraintViolationException(
					String.format("Không thể xóa quyền hạn '%s' vì đang được gán cho vai trò.", permission.getName()));
		}
	}
	
	public BulkOperationResult<Long> bulkDelete(Collection<Long> ids, String appCodeContext) {
		BulkOperationResult<Long> result = new BulkOperationResult<>();
		if (ids == null || ids.isEmpty()) return result;
		
		List<CorePermission> permissionsInDb = corePermissionService.findAllByIds(ids);
		Map<Long, CorePermission> permissionMap = permissionsInDb.stream().collect(Collectors.toMap(CorePermission::getId, p -> p));
		
		for (Long id : ids) {
			CorePermission permission = permissionMap.get(id);
			if (permission == null) {
				result.addFailure(id, "Quyền hạn không tồn tại.");
				continue;
			}
			try {
				validateDeletable(permission, appCodeContext);
				result.addSuccess(id);
			} catch (PermissionDeniedException | DataConstraintViolationException e) {
				result.addFailure(id, e.getMessage());
			} catch (Exception e) {
				log.error("Lỗi không xác định khi kiểm tra xóa quyền hạn ID {}: {}", id, e.getMessage());
				result.addFailure(id, "Lỗi hệ thống không xác định.");
			}
		}
		if (!result.getSuccessfulItems().isEmpty()) {
			corePermissionService.deleteByIds(result.getSuccessfulItems());
		}
		return result;
	}
	
	@Transactional(readOnly = true)
	public CorePermissionData findById(Long id, String appCodeContext) {
		CorePermission permission = corePermissionService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CorePermission.class, id));
		if (!permission.getAppCode().equals(appCodeContext)) {
			throw new PermissionDeniedException("Không có quyền xem quyền hạn thuộc ứng dụng khác.");
		}
		return corePermissionMapper.toData(permission);
	}
	
	@Transactional(readOnly = true)
	public PagedResult<CorePermissionData> findAll(CorePermissionSearchCriteria criteria, String appCodeContext) {
		criteria.setAppCode(appCodeContext);
		Pageable pageable = CoreUtils.getPageRequest(criteria);
		Page<CorePermission> page = corePermissionService.findAll(criteria, pageable);
		return PagedResult.from(page, corePermissionMapper::toData);
	}
}
