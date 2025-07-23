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
import vn.tr.core.dao.model.CoreUserType;
import vn.tr.core.dao.service.CoreUserAppService;
import vn.tr.core.dao.service.CoreUserTypeService;
import vn.tr.core.data.criteria.CoreUserTypeSearchCriteria;
import vn.tr.core.data.dto.CoreUserTypeData;
import vn.tr.core.data.mapper.CoreUserTypeMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lớp Business (Facade) điều phối các nghiệp vụ phức tạp liên quan đến Loại người dùng (CoreUserType).
 * Các thao tác ghi (Create, Update, Delete) yêu cầu quyền Super Admin.
 *
 * @author tyran8x
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CoreUserTypeBusiness {
	
	private final CoreUserTypeService coreUserTypeService;
	private final CoreUserAppService coreUserAppService; // Dependency để kiểm tra ràng buộc
	private final CoreUserTypeMapper coreUserTypeMapper;
	private final GenericUpsertHelper genericUpsertHelper;
	
	/**
	 * Tạo mới một loại người dùng. Yêu cầu quyền Super Admin.
	 *
	 * @param data         DTO chứa thông tin.
	 * @param isSuperAdmin Cờ xác định người dùng có phải là Super Admin không.
	 *
	 * @return Dữ liệu của loại người dùng sau khi đã được tạo.
	 */
	public CoreUserTypeData create(CoreUserTypeData data, boolean isSuperAdmin) {
		checkSuperAdminPermission(isSuperAdmin);
		return upsertByCode(data);
	}
	
	private void checkSuperAdminPermission(boolean isSuperAdmin) {
		if (!isSuperAdmin) {
			throw new PermissionDeniedException("Thao tác này yêu cầu quyền Super Admin.");
		}
	}
	
	private CoreUserTypeData upsertByCode(CoreUserTypeData data) {
		CoreUserType userType = genericUpsertHelper.upsert(
				data,
				() -> coreUserTypeService.findByCodeIgnoreCaseIncludingDeleted(data.getCode()),
				() -> coreUserTypeMapper.toEntity(data),
				coreUserTypeMapper::updateEntityFromData,
				coreUserTypeService.getRepository()
		                                                  );
		return coreUserTypeMapper.toData(userType);
	}
	
	/**
	 * Cập nhật thông tin một loại người dùng. Yêu cầu quyền Super Admin.
	 *
	 * @param id           ID của loại người dùng cần cập nhật.
	 * @param data         Dữ liệu mới.
	 * @param isSuperAdmin Cờ xác định người dùng có phải là Super Admin không.
	 *
	 * @return Dữ liệu của loại người dùng sau khi đã được cập nhật.
	 */
	public CoreUserTypeData update(Long id, CoreUserTypeData data, boolean isSuperAdmin) {
		checkSuperAdminPermission(isSuperAdmin);
		coreUserTypeService.findById(id).orElseThrow(() -> new EntityNotFoundException(CoreUserType.class, id));
		return upsertByCode(data);
	}
	
	/**
	 * Xóa một loại người dùng (xóa mềm). Yêu cầu quyền Super Admin.
	 *
	 * @param id           ID của loại người dùng cần xóa.
	 * @param isSuperAdmin Cờ xác định người dùng có phải là Super Admin không.
	 */
	public void delete(Long id, boolean isSuperAdmin) {
		checkSuperAdminPermission(isSuperAdmin);
		CoreUserType userType = coreUserTypeService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreUserType.class, id));
		validateDeletable(userType);
		coreUserTypeService.deleteByIds(List.of(id));
	}
	
	private void validateDeletable(CoreUserType userType) {
		if (coreUserAppService.isUserTypeInUse(userType.getCode())) {
			throw new DataConstraintViolationException(String.format("Không thể xóa loại người dùng '%s' vì đang được sử dụng.", userType.getName()));
		}
	}
	
	/**
	 * Xóa hàng loạt loại người dùng và trả về kết quả chi tiết. Yêu cầu quyền Super Admin.
	 *
	 * @param ids          Collection các ID cần xóa.
	 * @param isSuperAdmin Cờ xác định người dùng có phải là Super Admin không.
	 *
	 * @return Báo cáo chi tiết về kết quả xóa của từng ID.
	 */
	public BulkOperationResult<Long> bulkDelete(Collection<Long> ids, boolean isSuperAdmin) {
		checkSuperAdminPermission(isSuperAdmin);
		BulkOperationResult<Long> result = new BulkOperationResult<>();
		if (ids == null || ids.isEmpty()) return result;
		
		List<CoreUserType> userTypesInDb = coreUserTypeService.findAllByIds(ids);
		Map<Long, CoreUserType> userTypeMap = userTypesInDb.stream().collect(Collectors.toMap(CoreUserType::getId, ut -> ut));
		
		for (Long id : ids) {
			CoreUserType userType = userTypeMap.get(id);
			if (userType == null) {
				result.addFailure(id, "Loại người dùng không tồn tại.");
				continue;
			}
			try {
				validateDeletable(userType);
				result.addSuccess(id);
			} catch (DataConstraintViolationException e) {
				result.addFailure(id, e.getMessage());
			} catch (Exception e) {
				log.error("Lỗi không xác định khi kiểm tra xóa loại người dùng ID {}: {}", id, e.getMessage());
				result.addFailure(id, "Lỗi hệ thống không xác định.");
			}
		}
		if (!result.getSuccessfulItems().isEmpty()) {
			coreUserTypeService.deleteByIds(result.getSuccessfulItems());
		}
		return result;
	}
	
	/**
	 * Tìm và trả về thông tin chi tiết của một loại người dùng.
	 *
	 * @param id ID cần tìm.
	 *
	 * @return Dữ liệu chi tiết.
	 */
	@Transactional(readOnly = true)
	public CoreUserTypeData findById(Long id) {
		return coreUserTypeService.findById(id)
				.map(coreUserTypeMapper::toData)
				.orElseThrow(() -> new EntityNotFoundException(CoreUserType.class, id));
	}
	
	/**
	 * Tìm kiếm và trả về danh sách loại người dùng có phân trang.
	 *
	 * @param criteria Các tiêu chí để tìm kiếm.
	 *
	 * @return Kết quả phân trang.
	 */
	@Transactional(readOnly = true)
	public PagedResult<CoreUserTypeData> findAll(CoreUserTypeSearchCriteria criteria) {
		Pageable pageable = CoreUtils.getPageRequest(criteria);
		Page<CoreUserType> page = coreUserTypeService.findAll(criteria, pageable);
		return PagedResult.from(page, coreUserTypeMapper::toData);
	}
}
