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
import vn.tr.core.dao.model.CoreTag;
import vn.tr.core.dao.service.CoreTagAssignmentService;
import vn.tr.core.dao.service.CoreTagService;
import vn.tr.core.data.criteria.CoreTagSearchCriteria;
import vn.tr.core.data.dto.CoreTagData;
import vn.tr.core.data.mapper.CoreTagMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lớp Business (Facade) điều phối các nghiệp vụ phức tạp liên quan đến Quản lý Thẻ tag (CoreTag).
 * CoreTag là một tài nguyên toàn cục, các thao tác ghi (Create, Update, Delete) yêu cầu quyền Super Admin.
 *
 * @author tyran8x
 * @version 2.1
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CoreTagBusiness {
	
	private final CoreTagService coreTagService;
	private final CoreTagAssignmentService coreTagAssignmentService;
	private final CoreTagMapper coreTagMapper;
	private final GenericUpsertHelper genericUpsertHelper;
	
	/**
	 * Tạo mới một thẻ tag. Yêu cầu quyền Super Admin.
	 *
	 * @param data         DTO chứa thông tin của thẻ tag.
	 * @param isSuperAdmin Cờ xác định người dùng có phải là Super Admin không.
	 *
	 * @return Dữ liệu của thẻ tag sau khi đã được tạo.
	 *
	 * @throws PermissionDeniedException nếu không có quyền Super Admin.
	 */
	public CoreTagData create(CoreTagData data, boolean isSuperAdmin) {
		checkSuperAdminPermission(isSuperAdmin);
		return upsertByCode(data);
	}
	
	private void checkSuperAdminPermission(boolean isSuperAdmin) {
		if (!isSuperAdmin) {
			throw new PermissionDeniedException("Thao tác này yêu cầu quyền Super Admin.");
		}
	}
	
	private CoreTagData upsertByCode(CoreTagData data) {
		CoreTag tag = genericUpsertHelper.upsert(
				data,
				() -> coreTagService.findByCodeIgnoreCaseIncludingDeleted(data.getCode()),
				() -> coreTagMapper.toEntity(data),
				coreTagMapper::updateEntityFromData,
				coreTagService.getRepository()
		                                        );
		return coreTagMapper.toData(tag);
	}
	
	/**
	 * Cập nhật thông tin một thẻ tag. Yêu cầu quyền Super Admin.
	 *
	 * @param id           ID của thẻ tag cần cập nhật.
	 * @param data         Dữ liệu mới của thẻ tag.
	 * @param isSuperAdmin Cờ xác định người dùng có phải là Super Admin không.
	 *
	 * @return Dữ liệu của thẻ tag sau khi đã được cập nhật.
	 *
	 * @throws PermissionDeniedException nếu không có quyền Super Admin.
	 * @throws EntityNotFoundException   nếu không tìm thấy thẻ tag với ID tương ứng.
	 */
	public CoreTagData update(Long id, CoreTagData data, boolean isSuperAdmin) {
		checkSuperAdminPermission(isSuperAdmin);
		coreTagService.findById(id).orElseThrow(() -> new EntityNotFoundException(CoreTag.class, id));
		return upsertByCode(data);
	}
	
	/**
	 * Xóa một thẻ tag (xóa mềm). Yêu cầu quyền Super Admin.
	 *
	 * @param id           ID của thẻ tag cần xóa.
	 * @param isSuperAdmin Cờ xác định người dùng có phải là Super Admin không.
	 *
	 * @throws PermissionDeniedException        nếu không có quyền Super Admin.
	 * @throws EntityNotFoundException          nếu không tìm thấy thẻ tag.
	 * @throws DataConstraintViolationException nếu thẻ tag đang được sử dụng.
	 */
	public void delete(Long id, boolean isSuperAdmin) {
		checkSuperAdminPermission(isSuperAdmin);
		CoreTag tag = coreTagService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreTag.class, id));
		validateDeletable(tag);
		coreTagService.deleteByIds(List.of(id));
	}
	
	private void validateDeletable(CoreTag tag) {
		if (coreTagAssignmentService.isTagInUse(tag)) {
			throw new DataConstraintViolationException(String.format("Không thể xóa thẻ tag '%s' vì đang được sử dụng.", tag.getName()));
		}
	}
	
	/**
	 * Xóa hàng loạt thẻ tag (xóa mềm) và trả về kết quả chi tiết. Yêu cầu quyền Super Admin.
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
		
		List<CoreTag> tagsInDb = coreTagService.findAllByIds(ids);
		Map<Long, CoreTag> tagMap = tagsInDb.stream().collect(Collectors.toMap(CoreTag::getId, t -> t));
		
		for (Long id : ids) {
			CoreTag tag = tagMap.get(id);
			if (tag == null) {
				result.addFailure(id, "Thẻ tag không tồn tại.");
				continue;
			}
			try {
				validateDeletable(tag);
				result.addSuccess(id);
			} catch (DataConstraintViolationException e) {
				result.addFailure(id, e.getMessage());
			} catch (Exception e) {
				log.error("Lỗi không xác định khi kiểm tra xóa thẻ tag ID {}: {}", id, e.getMessage());
				result.addFailure(id, "Lỗi hệ thống không xác định.");
			}
		}
		if (!result.getSuccessfulItems().isEmpty()) {
			coreTagService.deleteByIds(result.getSuccessfulItems());
		}
		return result;
	}
	
	/**
	 * Tìm và trả về thông tin chi tiết của một thẻ tag.
	 *
	 * @param id ID của thẻ tag cần tìm.
	 *
	 * @return Dữ liệu chi tiết của thẻ tag.
	 *
	 * @throws EntityNotFoundException nếu không tìm thấy thẻ tag.
	 */
	@Transactional(readOnly = true)
	public CoreTagData findById(Long id) {
		return coreTagService.findById(id)
				.map(coreTagMapper::toData)
				.orElseThrow(() -> new EntityNotFoundException(CoreTag.class, id));
	}
	
	/**
	 * Tìm kiếm và trả về danh sách thẻ tag có phân trang.
	 *
	 * @param criteria Các tiêu chí để tìm kiếm.
	 *
	 * @return Kết quả phân trang của danh sách thẻ tag.
	 */
	@Transactional(readOnly = true)
	public PagedResult<CoreTagData> findAll(CoreTagSearchCriteria criteria) {
		Pageable pageable = CoreUtils.getPageRequest(criteria);
		Page<CoreTag> page = coreTagService.findAll(criteria, pageable);
		return PagedResult.from(page, coreTagMapper::toData);
	}
}
