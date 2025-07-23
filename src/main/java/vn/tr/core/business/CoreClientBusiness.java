package vn.tr.core.business;

import cn.hutool.core.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.core.exception.base.PermissionDeniedException;
import vn.tr.common.jpa.helper.GenericUpsertHelper;
import vn.tr.common.web.data.dto.BulkOperationResult;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.common.web.utils.PagedResult;
import vn.tr.core.dao.model.CoreClient;
import vn.tr.core.dao.service.CoreClientService;
import vn.tr.core.data.criteria.CoreClientSearchCriteria;
import vn.tr.core.data.dto.CoreClientData;
import vn.tr.core.data.mapper.CoreClientMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lớp Business (Facade) điều phối các nghiệp vụ phức tạp liên quan đến Quản lý Client (CoreClient).
 * Mọi thao tác đều được thực hiện trong ngữ cảnh của một ứng dụng (appCodeContext).
 *
 * @author tyran8x
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CoreClientBusiness {
	
	private final CoreClientService coreClientService;
	private final CoreClientMapper coreClientMapper;
	private final GenericUpsertHelper genericUpsertHelper;
	
	/**
	 * Tạo mới một client trong ngữ cảnh của một ứng dụng.
	 * Tự động tạo clientId, clientKey, clientSecret nếu chưa có.
	 *
	 * @param data           DTO chứa thông tin client.
	 * @param appCodeContext Mã của ứng dụng mà client thuộc về.
	 *
	 * @return Dữ liệu của client sau khi tạo.
	 */
	public CoreClientData create(CoreClientData data, String appCodeContext) {
		data.setAppCode(appCodeContext);
		
		// Tự động sinh các giá trị nếu cần
		if (data.getClientId() == null || data.getClientId().isBlank()) {
			data.setClientId(RandomUtil.randomString(32));
		}
		if (data.getClientKey() == null || data.getClientKey().isBlank()) {
			data.setClientKey(RandomUtil.randomString(16));
		}
		if (data.getClientSecret() == null || data.getClientSecret().isBlank()) {
			data.setClientSecret(RandomUtil.randomString(64));
		}
		
		return upsertByClientId(data, appCodeContext);
	}
	
	private CoreClientData upsertByClientId(CoreClientData data, String appCodeContext) {
		CoreClient client = genericUpsertHelper.upsert(
				data,
				() -> coreClientService.findByClientIdAndAppCodeIncludingDeleted(data.getClientId(), appCodeContext),
				() -> coreClientMapper.toEntity(data),
				coreClientMapper::updateEntityFromData,
				coreClientService.getRepository()
		                                              );
		client.setAppCode(appCodeContext);
		CoreClient savedClient = coreClientService.save(client);
		return coreClientMapper.toData(savedClient);
	}
	
	/**
	 * Cập nhật một client trong ngữ cảnh của một ứng dụng.
	 *
	 * @param id             ID của client cần cập nhật.
	 * @param data           DTO chứa thông tin mới.
	 * @param appCodeContext Mã của ứng dụng (để xác thực quyền).
	 *
	 * @return Dữ liệu của client sau khi cập nhật.
	 */
	public CoreClientData update(Long id, CoreClientData data, String appCodeContext) {
		CoreClient existing = coreClientService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreClient.class, id));
		if (!existing.getAppCode().equals(appCodeContext)) {
			throw new PermissionDeniedException("Không có quyền cập nhật client thuộc ứng dụng khác.");
		}
		data.setAppCode(appCodeContext);
		// Không cho phép thay đổi clientId khi cập nhật
		data.setClientId(existing.getClientId());
		return upsertByClientId(data, appCodeContext);
	}
	
	/**
	 * Xóa một client duy nhất (xóa mềm).
	 *
	 * @param id             ID của client cần xóa.
	 * @param appCodeContext Ngữ cảnh ứng dụng để kiểm tra quyền.
	 */
	public void delete(Long id, String appCodeContext) {
		CoreClient client = coreClientService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreClient.class, id));
		validateDeletable(client, appCodeContext);
		coreClientService.deleteByIds(List.of(id));
	}
	
	private void validateDeletable(CoreClient client, String appCodeContext) {
		if (!client.getAppCode().equals(appCodeContext)) {
			throw new PermissionDeniedException(String.format("Không có quyền xóa client '%s'.", client.getClientId()));
		}
		// Hiện tại chưa có ràng buộc nghiệp vụ khi xóa client.
		// Có thể bổ sung sau nếu cần (ví dụ: kiểm tra xem có token nào đang active không).
	}
	
	/**
	 * Xóa hàng loạt client và trả về kết quả chi tiết cho từng item.
	 *
	 * @param ids            Collection các ID cần xóa.
	 * @param appCodeContext Ngữ cảnh ứng dụng để kiểm tra quyền.
	 *
	 * @return Một đối tượng chứa danh sách các ID xóa thành công và thất bại.
	 */
	public BulkOperationResult<Long> bulkDelete(Collection<Long> ids, String appCodeContext) {
		BulkOperationResult<Long> result = new BulkOperationResult<>();
		if (ids == null || ids.isEmpty()) return result;
		
		List<CoreClient> clientsInDb = coreClientService.findAllByIds(ids);
		Map<Long, CoreClient> clientMap = clientsInDb.stream().collect(Collectors.toMap(CoreClient::getId, c -> c));
		
		for (Long id : ids) {
			CoreClient client = clientMap.get(id);
			if (client == null) {
				result.addFailure(id, "Client không tồn tại.");
				continue;
			}
			try {
				validateDeletable(client, appCodeContext);
				result.addSuccess(id);
			} catch (PermissionDeniedException e) {
				result.addFailure(id, e.getMessage());
			} catch (Exception e) {
				log.error("Lỗi không xác định khi kiểm tra xóa client ID {}: {}", id, e.getMessage());
				result.addFailure(id, "Lỗi hệ thống không xác định.");
			}
		}
		if (!result.getSuccessfulItems().isEmpty()) {
			coreClientService.deleteByIds(result.getSuccessfulItems());
		}
		return result;
	}
	
	/**
	 * Tìm và trả về thông tin chi tiết của một client.
	 *
	 * @param id             ID của client cần tìm.
	 * @param appCodeContext Ngữ cảnh ứng dụng để kiểm tra quyền.
	 *
	 * @return Dữ liệu chi tiết của client.
	 */
	@Transactional(readOnly = true)
	public CoreClientData findById(Long id, String appCodeContext) {
		CoreClient client = coreClientService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreClient.class, id));
		if (!client.getAppCode().equals(appCodeContext)) {
			throw new PermissionDeniedException("Không có quyền xem client thuộc ứng dụng khác.");
		}
		return coreClientMapper.toData(client);
	}
	
	/**
	 * Tìm kiếm và trả về danh sách client có phân trang.
	 *
	 * @param criteria       Các tiêu chí tìm kiếm.
	 * @param appCodeContext Ngữ cảnh ứng dụng để lọc kết quả.
	 *
	 * @return Kết quả phân trang của danh sách client.
	 */
	@Transactional(readOnly = true)
	public PagedResult<CoreClientData> findAll(CoreClientSearchCriteria criteria, String appCodeContext) {
		criteria.setAppCode(appCodeContext);
		Pageable pageable = CoreUtils.getPageRequest(criteria);
		Page<CoreClient> page = coreClientService.findAll(criteria, pageable);
		return PagedResult.from(page, coreClientMapper::toData);
	}
}
