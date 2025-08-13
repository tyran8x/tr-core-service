package vn.tr.core.business;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.exception.base.DataConstraintViolationException;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.core.exception.base.InvalidEntityException;
import vn.tr.common.core.exception.base.PermissionDeniedException;
import vn.tr.common.jpa.helper.GenericUpsertHelper;
import vn.tr.common.web.data.dto.BulkOperationResult;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.common.web.utils.PagedResult;
import vn.tr.core.dao.model.CoreGroup;
import vn.tr.core.dao.service.CoreGroupService;
import vn.tr.core.dao.service.CoreUserGroupService;
import vn.tr.core.data.criteria.CoreGroupSearchCriteria;
import vn.tr.core.data.dto.CoreGroupData;
import vn.tr.core.data.mapper.CoreGroupMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lớp Business (Facade) điều phối các nghiệp vụ phức tạp liên quan đến Quản lý Nhóm (CoreGroup). Mọi thao tác đều được thực hiện trong ngữ cảnh của
 * một ứng dụng (appCodeContext) để đảm bảo phân quyền.
 *
 * @author tyran8x
 * @version 2.4 (Final with Javadoc and Custom Exceptions)
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CoreGroupBusiness {
	
	private final CoreGroupService coreGroupService;
	private final CoreUserGroupService coreUserGroupService;
	private final CoreGroupMapper coreGroupMapper;
	private final GenericUpsertHelper genericUpsertHelper;
	
	/**
	 * Tạo mới một nhóm trong ngữ cảnh của một ứng dụng.
	 *
	 * @param data           DTO chứa thông tin nhóm.
	 * @param appCodeContext Mã của ứng dụng mà nhóm thuộc về.
	 *
	 * @return Dữ liệu của nhóm sau khi tạo.
	 */
	public CoreGroupData create(CoreGroupData data, String appCodeContext) {
		data.setAppCode(appCodeContext);
		return upsertByCode(data, appCodeContext);
	}
	
	private CoreGroupData upsertByCode(CoreGroupData data, String appCodeContext) {
		CoreGroup group = genericUpsertHelper.upsert(
				data,
				() -> coreGroupService.findByCodeAndAppCodeIncludingDeleted(data.getCode(), appCodeContext),
				() -> coreGroupMapper.toEntity(data),
				coreGroupMapper::updateEntityFromData,
				coreGroupService.getRepository()
		                                            );
		
		group.setAppCode(appCodeContext);
		
		if (data.getParentId() != null) {
			CoreGroup parent = coreGroupService.findById(data.getParentId())
					.orElseThrow(() -> new InvalidEntityException("Nhóm cha với ID " + data.getParentId() + " không tồn tại."));
			if (!parent.getAppCode().equals(appCodeContext)) {
				throw new InvalidEntityException("Nhóm cha phải thuộc cùng một ứng dụng.");
			}
			group.setParentId(data.getParentId());
		} else {
			group.setParentId(null);
		}
		
		CoreGroup savedGroup = coreGroupService.save(group);
		return coreGroupMapper.toData(savedGroup);
	}
	
	/**
	 * Cập nhật một nhóm trong ngữ cảnh của một ứng dụng.
	 *
	 * @param id             ID của nhóm cần cập nhật.
	 * @param data           DTO chứa thông tin mới.
	 * @param appCodeContext Mã của ứng dụng (để xác thực quyền).
	 *
	 * @return Dữ liệu của nhóm sau khi cập nhật.
	 */
	public CoreGroupData update(Long id, CoreGroupData data, String appCodeContext) {
		CoreGroup existingGroup = coreGroupService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreGroup.class, id));
		
		// Kiểm tra quyền sở hữu trước khi thực hiện
		if (!existingGroup.getAppCode().equals(appCodeContext)) {
			throw new PermissionDeniedException("Không có quyền cập nhật nhóm thuộc ứng dụng khác.");
		}
		
		data.setAppCode(appCodeContext);
		return upsertByCode(data, appCodeContext);
	}
	
	/**
	 * Tìm và trả về thông tin chi tiết của một nhóm.
	 *
	 * @param id             ID của nhóm cần tìm.
	 * @param appCodeContext Ngữ cảnh ứng dụng để kiểm tra quyền.
	 *
	 * @return Dữ liệu chi tiết của nhóm.
	 *
	 * @throws EntityNotFoundException   nếu không tìm thấy nhóm.
	 * @throws PermissionDeniedException nếu không có quyền xem nhóm.
	 */
	@Transactional(readOnly = true)
	public CoreGroupData findById(Long id, String appCodeContext) {
		CoreGroup group = coreGroupService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreGroup.class, id));
		
		if (!group.getAppCode().equals(appCodeContext)) {
			throw new PermissionDeniedException("Không có quyền xem nhóm thuộc ứng dụng khác.");
		}
		return coreGroupMapper.toData(group);
	}
	
	/**
	 * Xóa một nhóm duy nhất. Ném ra exception ngay lập tức nếu có bất kỳ lỗi nào (Fail-Fast).
	 *
	 * @param id             ID của nhóm cần xóa.
	 * @param appCodeContext Ngữ cảnh ứng dụng để kiểm tra quyền.
	 */
	public void delete(Long id, String appCodeContext) {
		CoreGroup group = coreGroupService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreGroup.class, id));
		
		validateDeletable(group, appCodeContext);
		
		coreGroupService.delete(id);
	}
	
	private void validateDeletable(CoreGroup group, String appCodeContext) {
		if (!group.getAppCode().equals(appCodeContext)) {
			throw new PermissionDeniedException(String.format("Không có quyền xóa nhóm '%s' (ID: %d).", group.getName(), group.getId()));
		}
		
		if (coreUserGroupService.isGroupInUse(group)) {
			throw new DataConstraintViolationException(String.format("Không thể xóa nhóm '%s' vì đang có người dùng thuộc nhóm.", group.getName()));
		}
	}
	
	/**
	 * Xóa hàng loạt nhóm và trả về kết quả chi tiết cho từng item.
	 *
	 * @param ids            Collection các ID cần xóa.
	 * @param appCodeContext Ngữ cảnh ứng dụng để kiểm tra quyền.
	 *
	 * @return Một đối tượng chứa danh sách các ID xóa thành công và thất bại.
	 */
	public BulkOperationResult<Long> bulkDelete(Collection<Long> ids, String appCodeContext) {
		BulkOperationResult<Long> result = new BulkOperationResult<>();
		if (ids == null || ids.isEmpty()) {
			return result;
		}
		
		List<CoreGroup> groupsInDb = coreGroupService.findAllByIds(ids);
		Map<Long, CoreGroup> groupMap = groupsInDb.stream().collect(Collectors.toMap(CoreGroup::getId, g -> g));
		
		for (Long id : ids) {
			CoreGroup group = groupMap.get(id);
			
			if (group == null) {
				result.addFailure(id, "Nhóm không tồn tại.");
				continue;
			}
			try {
				validateDeletable(group, appCodeContext);
				result.addSuccess(id);
			} catch (PermissionDeniedException | DataConstraintViolationException e) {
				result.addFailure(id, e.getMessage());
			} catch (Exception e) {
				log.error("Lỗi không xác định khi kiểm tra xóa nhóm ID {}: {}", id, e.getMessage());
				result.addFailure(id, "Lỗi hệ thống không xác định.");
			}
		}
		
		if (!result.getSuccessfulItems().isEmpty()) {
			coreGroupService.deleteByIds(result.getSuccessfulItems());
		}
		
		return result;
	}
	
	// =================================================================================================================
	// Private Helper Methods
	// =================================================================================================================
	
	/**
	 * Tìm kiếm và trả về danh sách nhóm có phân trang.
	 *
	 * @param criteria       Các tiêu chí để tìm kiếm.
	 * @param appCodeContext Ngữ cảnh ứng dụng để lọc kết quả.
	 *
	 * @return Kết quả phân trang của danh sách nhóm.
	 */
	@Transactional(readOnly = true)
	public PagedResult<CoreGroupData> findAll(CoreGroupSearchCriteria criteria, String appCodeContext) {
		criteria.setAppCode(appCodeContext);
		Pageable pageable = CoreUtils.getPageRequest(criteria);
		Page<CoreGroup> pageCoreGroup = coreGroupService.findAll(criteria, pageable);
		return PagedResult.from(pageCoreGroup, coreGroupMapper::toData);
	}
	
	/**
	 * Lấy toàn bộ danh sách nhóm (không phân trang) theo tiêu chí.
	 *
	 * @param criteria       Các tiêu chí để lọc.
	 * @param appCodeContext Ngữ cảnh ứng dụng để lọc kết quả.
	 *
	 * @return Danh sách đầy đủ các nhóm.
	 */
	@Transactional(readOnly = true)
	public List<CoreGroupData> getAll(CoreGroupSearchCriteria criteria, String appCodeContext) {
		criteria.setAppCode(appCodeContext);
		List<CoreGroup> groupList = coreGroupService.findAll(criteria);
		return coreGroupMapper.toData(groupList);
	}
}
