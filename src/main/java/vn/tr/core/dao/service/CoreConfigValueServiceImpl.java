package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreConfigValue;

import java.util.List;
import java.util.Optional;

/**
 * Lớp triển khai cho CoreConfigValueService.
 *
 * @author tyran8x
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CoreConfigValueServiceImpl implements CoreConfigValueService {
	
	private final CoreConfigValueRepo coreConfigValueRepo;
	
	@Override
	@Transactional
	public CoreConfigValue upsertValue(Long definitionId, String scopeType, String scopeValue, String newValue) {
		// 1. Tìm kiếm bản ghi, bao gồm cả đã xóa mềm, để có thể kích hoạt lại.
		List<CoreConfigValue> results = coreConfigValueRepo.findByDefinitionAndScopeIncludingDeletedSorted(definitionId, scopeType, scopeValue);
		
		CoreConfigValue configValue;
		
		if (results.isEmpty()) {
			// Trường hợp 1: Tạo mới hoàn toàn
			configValue = CoreConfigValue.builder()
					.definitionId(definitionId)
					.scopeType(scopeType)
					.scopeValue(scopeValue)
					.build();
		} else {
			// Trường hợp 2: Đã tồn tại, lấy bản ghi ưu tiên nhất
			configValue = results.get(0);
			if (results.size() > 1) {
				log.warn("DỮ LIỆU TRÙNG LẶP: Tìm thấy {} bản ghi CoreConfigValue cho defId={}, scopeType={}, scopeValue={}. Sử dụng bản ghi ID={}",
						results.size(), definitionId, scopeType, scopeValue, configValue.getId());
			}
			// Kích hoạt lại nếu nó đang bị xóa mềm
			if (configValue.getDeletedAt() != null) {
				configValue.setDeletedAt(null);
			}
		}
		
		// 2. Cập nhật giá trị mới và lưu lại
		configValue.setValue(newValue);
		return coreConfigValueRepo.save(configValue);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<CoreConfigValue> findActiveValue(Long definitionId, String scopeType, String scopeValue) {
		return coreConfigValueRepo.findByDefinitionIdAndScopeTypeAndScopeValue(definitionId, scopeType, scopeValue);
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean isDefinitionInUse(Long definitionId) {
		return coreConfigValueRepo.existsByDefinitionId(definitionId);
	}
	
	@Override
	public JpaRepository<CoreConfigValue, Long> getRepository() {
		return this.coreConfigValueRepo;
	}
}
