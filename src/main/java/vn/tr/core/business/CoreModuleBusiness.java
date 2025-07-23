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
import vn.tr.core.dao.model.CoreModule;
import vn.tr.core.dao.service.CoreModuleService;
import vn.tr.core.dao.service.CorePermissionService;
import vn.tr.core.data.criteria.CoreModuleSearchCriteria;
import vn.tr.core.data.dto.CoreModuleData;
import vn.tr.core.data.mapper.CoreModuleMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lớp Business (Facade) điều phối các nghiệp vụ phức tạp liên quan đến Quản lý Module (CoreModule).
 * Mọi thao tác đều được thực hiện trong ngữ cảnh của một ứng dụng (appCodeContext) để đảm bảo phân quyền.
 *
 * @author tyran8x
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CoreModuleBusiness {
	
	private final CoreModuleService coreModuleService;
	private final CorePermissionService corePermissionService;
	private final CoreModuleMapper coreModuleMapper;
	private final GenericUpsertHelper genericUpsertHelper;
	
	/**
	 * Tạo mới một module trong ngữ cảnh của một ứng dụng.
	 *
	 * @param data           DTO chứa thông tin module.
	 * @param appCodeContext Mã của ứng dụng mà module thuộc về.
	 *
	 * @return Dữ liệu của module sau khi tạo.
	 */
	public CoreModuleData create(CoreModuleData data, String appCodeContext) {
		data.setAppCode(appCodeContext);
		return upsertByCode(data, appCodeContext);
	}
	
	private CoreModuleData upsertByCode(CoreModuleData data, String appCodeContext) {
		CoreModule module = genericUpsertHelper.upsert(
				data,
				() -> coreModuleService.findByCodeAndAppCodeIncludingDeleted(data.getCode(), appCodeContext),
				() -> coreModuleMapper.toEntity(data),
				coreModuleMapper::updateEntityFromData,
				coreModuleService.getRepository()
		                                              );
		module.setAppCode(appCodeContext);
		CoreModule savedModule = coreModuleService.save(module);
		return coreModuleMapper.toData(savedModule);
	}
	
	/**
	 * Cập nhật một module trong ngữ cảnh của một ứng dụng.
	 *
	 * @param id             ID của module cần cập nhật.
	 * @param data           DTO chứa thông tin mới.
	 * @param appCodeContext Mã của ứng dụng (để xác thực quyền).
	 *
	 * @return Dữ liệu của module sau khi cập nhật.
	 *
	 * @throws PermissionDeniedException nếu không có quyền cập nhật module.
	 * @throws EntityNotFoundException   nếu không tìm thấy module.
	 */
	public CoreModuleData update(Long id, CoreModuleData data, String appCodeContext) {
		CoreModule existing = coreModuleService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreModule.class, id));
		if (!existing.getAppCode().equals(appCodeContext)) {
			throw new PermissionDeniedException("Không có quyền cập nhật module thuộc ứng dụng khác.");
		}
		data.setAppCode(appCodeContext);
		return upsertByCode(data, appCodeContext);
	}
	
	/**
	 * Xóa một module duy nhất (xóa mềm).
	 *
	 * @param id             ID của module cần xóa.
	 * @param appCodeContext Ngữ cảnh ứng dụng để kiểm tra quyền.
	 *
	 * @throws PermissionDeniedException        nếu không có quyền xóa.
	 * @throws EntityNotFoundException          nếu không tìm thấy module.
	 * @throws DataConstraintViolationException nếu module đang được sử dụng.
	 */
	public void delete(Long id, String appCodeContext) {
		CoreModule module = coreModuleService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreModule.class, id));
		validateDeletable(module, appCodeContext);
		coreModuleService.deleteByIds(List.of(id));
	}
	
	private void validateDeletable(CoreModule module, String appCodeContext) {
		if (!module.getAppCode().equals(appCodeContext)) {
			throw new PermissionDeniedException(String.format("Không có quyền xóa module '%s'.", module.getName()));
		}
		if (corePermissionService.isModuleInUse(module.getId())) {
			throw new DataConstraintViolationException(
					String.format("Không thể xóa module '%s' vì đang có quyền hạn được gán vào.", module.getName()));
		}
	}
	
	/**
	 * Xóa hàng loạt module và trả về kết quả chi tiết cho từng item.
	 *
	 * @param ids            Collection các ID cần xóa.
	 * @param appCodeContext Ngữ cảnh ứng dụng để kiểm tra quyền.
	 *
	 * @return Một đối tượng chứa danh sách các ID xóa thành công và thất bại.
	 */
	public BulkOperationResult<Long> bulkDelete(Collection<Long> ids, String appCodeContext) {
		BulkOperationResult<Long> result = new BulkOperationResult<>();
		if (ids == null || ids.isEmpty()) return result;
		
		List<CoreModule> modulesInDb = coreModuleService.findAllByIds(ids);
		Map<Long, CoreModule> moduleMap = modulesInDb.stream().collect(Collectors.toMap(CoreModule::getId, m -> m));
		
		for (Long id : ids) {
			CoreModule module = moduleMap.get(id);
			if (module == null) {
				result.addFailure(id, "Module không tồn tại.");
				continue;
			}
			try {
				validateDeletable(module, appCodeContext);
				result.addSuccess(id);
			} catch (PermissionDeniedException | DataConstraintViolationException e) {
				result.addFailure(id, e.getMessage());
			} catch (Exception e) {
				log.error("Lỗi không xác định khi kiểm tra xóa module ID {}: {}", id, e.getMessage());
				result.addFailure(id, "Lỗi hệ thống không xác định.");
			}
		}
		if (!result.getSuccessfulItems().isEmpty()) {
			coreModuleService.deleteByIds(result.getSuccessfulItems());
		}
		return result;
	}
	
	/**
	 * Tìm và trả về thông tin chi tiết của một module.
	 *
	 * @param id             ID của module cần tìm.
	 * @param appCodeContext Ngữ cảnh ứng dụng để kiểm tra quyền.
	 *
	 * @return Dữ liệu chi tiết của module.
	 *
	 * @throws EntityNotFoundException   nếu không tìm thấy module.
	 * @throws PermissionDeniedException nếu không có quyền xem module.
	 */
	@Transactional(readOnly = true)
	public CoreModuleData findById(Long id, String appCodeContext) {
		CoreModule module = coreModuleService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreModule.class, id));
		if (!module.getAppCode().equals(appCodeContext)) {
			throw new PermissionDeniedException("Không có quyền xem module thuộc ứng dụng khác.");
		}
		return coreModuleMapper.toData(module);
	}
	
	/**
	 * Tìm kiếm và trả về danh sách module có phân trang.
	 *
	 * @param criteria       Các tiêu chí tìm kiếm.
	 * @param appCodeContext Ngữ cảnh ứng dụng để lọc kết quả.
	 *
	 * @return Kết quả phân trang của danh sách module.
	 */
	@Transactional(readOnly = true)
	public PagedResult<CoreModuleData> findAll(CoreModuleSearchCriteria criteria, String appCodeContext) {
		criteria.setAppCode(appCodeContext);
		Pageable pageable = CoreUtils.getPageRequest(criteria);
		Page<CoreModule> page = coreModuleService.findAll(criteria, pageable);
		return PagedResult.from(page, coreModuleMapper::toData);
	}
}
