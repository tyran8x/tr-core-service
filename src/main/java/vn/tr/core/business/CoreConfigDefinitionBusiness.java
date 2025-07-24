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
import vn.tr.core.dao.model.CoreConfigDefinition;
import vn.tr.core.dao.service.CoreConfigDefinitionService;
import vn.tr.core.dao.service.CoreConfigValueService;
import vn.tr.core.data.criteria.CoreConfigDefinitionSearchCriteria;
import vn.tr.core.data.dto.CoreConfigDefinitionData;
import vn.tr.core.data.mapper.CoreConfigDefinitionMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lớp Business (Facade) điều phối các nghiệp vụ phức tạp liên quan đến Định nghĩa Cấu hình (CoreConfigDefinition).
 * Mọi thao tác đều được thực hiện trong ngữ cảnh của một ứng dụng (appCodeContext).
 *
 * @author tyran8x
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CoreConfigDefinitionBusiness {
	
	private final CoreConfigDefinitionService coreConfigDefinitionService;
	private final CoreConfigValueService valueService;
	private final CoreConfigDefinitionMapper coreConfigDefinitionMapper;
	private final GenericUpsertHelper genericUpsertHelper;
	
	/**
	 * Tạo mới một định nghĩa cấu hình trong ngữ cảnh của một ứng dụng.
	 *
	 * @param data           DTO chứa thông tin định nghĩa.
	 * @param appCodeContext Mã của ứng dụng mà định nghĩa thuộc về.
	 *
	 * @return Dữ liệu của định nghĩa sau khi tạo.
	 */
	public CoreConfigDefinitionData create(CoreConfigDefinitionData data, String appCodeContext) {
		data.setAppCode(appCodeContext);
		return upsertByKey(data, appCodeContext);
	}
	
	private CoreConfigDefinitionData upsertByKey(CoreConfigDefinitionData data, String appCodeContext) {
		CoreConfigDefinition definition = genericUpsertHelper.upsert(
				data,
				() -> coreConfigDefinitionService.findByKeyAndAppCodeIncludingDeleted(data.getKey(), appCodeContext),
				() -> coreConfigDefinitionMapper.toEntity(data),
				coreConfigDefinitionMapper::updateEntityFromData,
				coreConfigDefinitionService.getRepository()
		                                                            );
		definition.setAppCode(appCodeContext);
		CoreConfigDefinition saved = coreConfigDefinitionService.save(definition);
		return coreConfigDefinitionMapper.toData(saved);
	}
	
	/**
	 * Cập nhật một định nghĩa cấu hình trong ngữ cảnh của một ứng dụng.
	 *
	 * @param id             ID của định nghĩa cần cập nhật.
	 * @param data           DTO chứa thông tin mới.
	 * @param appCodeContext Mã của ứng dụng (để xác thực quyền).
	 *
	 * @return Dữ liệu của định nghĩa sau khi cập nhật.
	 */
	public CoreConfigDefinitionData update(Long id, CoreConfigDefinitionData data, String appCodeContext) {
		CoreConfigDefinition existing = coreConfigDefinitionService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreConfigDefinition.class, id));
		if (!existing.getAppCode().equals(appCodeContext)) {
			throw new PermissionDeniedException("Không có quyền cập nhật định nghĩa cấu hình thuộc ứng dụng khác.");
		}
		data.setAppCode(appCodeContext);
		data.setKey(existing.getKey()); // Key là bất biến, không cho phép thay đổi khi cập nhật.
		return upsertByKey(data, appCodeContext);
	}
	
	/**
	 * Xóa một định nghĩa cấu hình duy nhất (xóa mềm).
	 *
	 * @param id             ID của định nghĩa cần xóa.
	 * @param appCodeContext Ngữ cảnh ứng dụng để kiểm tra quyền.
	 */
	public void delete(Long id, String appCodeContext) {
		CoreConfigDefinition definition = coreConfigDefinitionService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreConfigDefinition.class, id));
		validateDeletable(definition, appCodeContext);
		coreConfigDefinitionService.deleteByIds(List.of(id));
	}
	
	private void validateDeletable(CoreConfigDefinition definition, String appCodeContext) {
		if (!definition.getAppCode().equals(appCodeContext)) {
			throw new PermissionDeniedException(String.format("Không có quyền xóa định nghĩa '%s'.", definition.getName()));
		}
		if (valueService.isDefinitionInUse(definition.getId())) {
			throw new DataConstraintViolationException(
					String.format("Không thể xóa định nghĩa '%s' vì đang có giá trị được cấu hình.", definition.getName()));
		}
	}
	
	/**
	 * Xóa hàng loạt định nghĩa cấu hình và trả về kết quả chi tiết cho từng item.
	 *
	 * @param ids            Collection các ID cần xóa.
	 * @param appCodeContext Ngữ cảnh ứng dụng để kiểm tra quyền.
	 *
	 * @return Một đối tượng chứa danh sách các ID xóa thành công và thất bại.
	 */
	public BulkOperationResult<Long> bulkDelete(Collection<Long> ids, String appCodeContext) {
		BulkOperationResult<Long> result = new BulkOperationResult<>();
		if (ids == null || ids.isEmpty()) return result;
		
		List<CoreConfigDefinition> definitionsInDb = coreConfigDefinitionService.findAllByIds(ids);
		Map<Long, CoreConfigDefinition> definitionMap = definitionsInDb.stream().collect(Collectors.toMap(CoreConfigDefinition::getId, d -> d));
		
		for (Long id : ids) {
			CoreConfigDefinition definition = definitionMap.get(id);
			if (definition == null) {
				result.addFailure(id, "Định nghĩa cấu hình không tồn tại.");
				continue;
			}
			try {
				validateDeletable(definition, appCodeContext);
				result.addSuccess(id);
			} catch (PermissionDeniedException | DataConstraintViolationException e) {
				result.addFailure(id, e.getMessage());
			} catch (Exception e) {
				log.error("Lỗi không xác định khi kiểm tra xóa định nghĩa ID {}: {}", id, e.getMessage());
				result.addFailure(id, "Lỗi hệ thống không xác định.");
			}
		}
		if (!result.getSuccessfulItems().isEmpty()) {
			coreConfigDefinitionService.deleteByIds(result.getSuccessfulItems());
		}
		return result;
	}
	
	/**
	 * Tìm và trả về thông tin chi tiết của một định nghĩa cấu hình.
	 *
	 * @param id             ID của định nghĩa cần tìm.
	 * @param appCodeContext Ngữ cảnh ứng dụng để kiểm tra quyền.
	 *
	 * @return Dữ liệu chi tiết của định nghĩa.
	 */
	@Transactional(readOnly = true)
	public CoreConfigDefinitionData findById(Long id, String appCodeContext) {
		CoreConfigDefinition definition = coreConfigDefinitionService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreConfigDefinition.class, id));
		if (!definition.getAppCode().equals(appCodeContext)) {
			throw new PermissionDeniedException("Không có quyền xem định nghĩa cấu hình thuộc ứng dụng khác.");
		}
		return coreConfigDefinitionMapper.toData(definition);
	}
	
	/**
	 * Tìm kiếm và trả về danh sách định nghĩa cấu hình có phân trang.
	 *
	 * @param criteria       Các tiêu chí tìm kiếm.
	 * @param appCodeContext Ngữ cảnh ứng dụng để lọc kết quả.
	 *
	 * @return Kết quả phân trang.
	 */
	@Transactional(readOnly = true)
	public PagedResult<CoreConfigDefinitionData> findAll(CoreConfigDefinitionSearchCriteria criteria, String appCodeContext) {
		criteria.setAppCode(appCodeContext);
		Pageable pageable = CoreUtils.getPageRequest(criteria);
		Page<CoreConfigDefinition> page = coreConfigDefinitionService.findAll(criteria, pageable);
		return PagedResult.from(page, coreConfigDefinitionMapper::toData);
	}
}
