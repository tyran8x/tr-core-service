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
import vn.tr.core.dao.model.CoreApp;
import vn.tr.core.dao.service.CoreAppService;
import vn.tr.core.dao.service.CoreUserAppService;
import vn.tr.core.data.criteria.CoreAppSearchCriteria;
import vn.tr.core.data.dto.CoreAppData;
import vn.tr.core.data.mapper.CoreAppMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lớp Business (Facade) điều phối các nghiệp vụ phức tạp liên quan đến Quản lý Ứng dụng (CoreApp).
 * Các thao tác ghi (Create, Update, Delete) yêu cầu quyền Super Admin.
 *
 * @author tyran8x
 * @version 2.4
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CoreAppBusiness {
	
	private final CoreAppService coreAppService;
	private final CoreUserAppService coreUserAppService;
	private final CoreAppMapper coreAppMapper;
	private final GenericUpsertHelper genericUpsertHelper;
	
	/**
	 * Tạo mới một ứng dụng. Yêu cầu quyền Super Admin.
	 *
	 * @param coreAppData  Dữ liệu của ứng dụng cần tạo.
	 * @param isSuperAdmin Cờ xác định người dùng có phải là Super Admin không.
	 *
	 * @return Dữ liệu của ứng dụng sau khi đã được tạo.
	 *
	 * @throws PermissionDeniedException nếu không có quyền Super Admin.
	 */
	public CoreAppData create(CoreAppData coreAppData, boolean isSuperAdmin) {
		checkSuperAdminPermission(isSuperAdmin);
		return upsertByCode(coreAppData);
	}
	
	private void checkSuperAdminPermission(boolean isSuperAdmin) {
		if (!isSuperAdmin) {
			throw new PermissionDeniedException("Thao tác này yêu cầu quyền Super Admin.");
		}
	}
	
	private CoreAppData upsertByCode(CoreAppData data) {
		CoreApp app = genericUpsertHelper.upsert(
				data,
				() -> coreAppService.findByCodeIgnoreCaseIncludingDeleted(data.getCode()),
				() -> coreAppMapper.toEntity(data),
				coreAppMapper::updateEntityFromData,
				coreAppService.getRepository()
		                                        );
		return coreAppMapper.toData(app);
	}
	
	/**
	 * Cập nhật thông tin một ứng dụng. Yêu cầu quyền Super Admin.
	 *
	 * @param id           ID của ứng dụng cần cập nhật.
	 * @param coreAppData  Dữ liệu mới của ứng dụng.
	 * @param isSuperAdmin Cờ xác định người dùng có phải là Super Admin không.
	 *
	 * @return Dữ liệu của ứng dụng sau khi đã được cập nhật.
	 *
	 * @throws PermissionDeniedException nếu không có quyền Super Admin.
	 * @throws EntityNotFoundException   nếu không tìm thấy ứng dụng với ID tương ứng.
	 */
	public CoreAppData update(Long id, CoreAppData coreAppData, boolean isSuperAdmin) {
		checkSuperAdminPermission(isSuperAdmin);
		coreAppService.findById(id).orElseThrow(() -> new EntityNotFoundException(CoreApp.class, id));
		return upsertByCode(coreAppData);
	}
	
	/**
	 * Xóa một ứng dụng (xóa mềm). Yêu cầu quyền Super Admin.
	 *
	 * @param id           ID của ứng dụng cần xóa.
	 * @param isSuperAdmin Cờ xác định người dùng có phải là Super Admin không.
	 *
	 * @throws PermissionDeniedException        nếu không có quyền Super Admin.
	 * @throws EntityNotFoundException          nếu không tìm thấy ứng dụng.
	 * @throws DataConstraintViolationException nếu ứng dụng đang được sử dụng.
	 */
	public void delete(Long id, boolean isSuperAdmin) {
		checkSuperAdminPermission(isSuperAdmin);
		CoreApp app = coreAppService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreApp.class, id));
		validateDeletable(app);
		coreAppService.delete(id);
	}
	
	private void validateDeletable(CoreApp app) {
		if (coreUserAppService.isAppInUse(app.getCode())) {
			throw new DataConstraintViolationException(
					String.format("Không thể xóa ứng dụng '%s' vì đang có người dùng được gán vào.", app.getName()));
		}
	}
	
	/**
	 * Xóa hàng loạt ứng dụng (xóa mềm) và trả về kết quả chi tiết. Yêu cầu quyền Super Admin.
	 *
	 * @param ids          Collection các ID cần xóa.
	 * @param isSuperAdmin Cờ xác định người dùng có phải là Super Admin không.
	 *
	 * @return Một đối tượng chứa danh sách các ID xóa thành công và thất bại.
	 *
	 * @throws PermissionDeniedException nếu không có quyền Super Admin.
	 */
	public BulkOperationResult<Long> bulkDelete(Collection<Long> ids, boolean isSuperAdmin) {
		checkSuperAdminPermission(isSuperAdmin);
		BulkOperationResult<Long> result = new BulkOperationResult<>();
		if (ids == null || ids.isEmpty()) return result;
		
		List<CoreApp> appsInDb = coreAppService.findAllByIds(ids);
		Map<Long, CoreApp> appMap = appsInDb.stream().collect(Collectors.toMap(CoreApp::getId, app -> app));
		
		for (Long id : ids) {
			CoreApp app = appMap.get(id);
			if (app == null) {
				result.addFailure(id, "Ứng dụng không tồn tại.");
				continue;
			}
			try {
				validateDeletable(app);
				result.addSuccess(id);
			} catch (DataConstraintViolationException e) {
				result.addFailure(id, e.getMessage());
			} catch (Exception e) {
				log.error("Lỗi không xác định khi kiểm tra xóa ứng dụng ID {}: {}", id, e.getMessage());
				result.addFailure(id, "Lỗi hệ thống không xác định.");
			}
		}
		if (!result.getSuccessfulItems().isEmpty()) {
			coreAppService.deleteByIds(result.getSuccessfulItems());
		}
		return result;
	}
	
	/**
	 * Tìm và trả về thông tin chi tiết của một ứng dụng.
	 *
	 * @param id ID của ứng dụng cần tìm.
	 *
	 * @return Dữ liệu chi tiết của ứng dụng.
	 *
	 * @throws EntityNotFoundException nếu không tìm thấy ứng dụng.
	 */
	@Transactional(readOnly = true)
	public CoreAppData findById(Long id) {
		return coreAppService.findById(id)
				.map(coreAppMapper::toData)
				.orElseThrow(() -> new EntityNotFoundException(CoreApp.class, id));
	}
	
	/**
	 * Tìm kiếm và trả về danh sách ứng dụng có phân trang.
	 *
	 * @param criteria Các tiêu chí để tìm kiếm.
	 *
	 * @return Kết quả phân trang của danh sách ứng dụng.
	 */
	@Transactional(readOnly = true)
	public PagedResult<CoreAppData> findAll(CoreAppSearchCriteria criteria) {
		Pageable pageable = CoreUtils.getPageRequest(criteria);
		Page<CoreApp> pageCoreApp = coreAppService.findAll(criteria, pageable);
		return PagedResult.from(pageCoreApp, coreAppMapper::toData);
	}
}
