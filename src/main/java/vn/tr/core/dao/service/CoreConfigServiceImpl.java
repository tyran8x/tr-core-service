package vn.tr.core.dao.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.enums.ConfigScope;
import vn.tr.common.core.exception.base.InvalidEntityException;
import vn.tr.common.encrypt.utils.EncryptUtils;
import vn.tr.core.dao.model.CoreConfigDefinition;
import vn.tr.core.dao.model.CoreConfigValue;

import java.util.Map;
import java.util.Optional;

/**
 * Lớp triển khai cho CoreConfigService.
 *
 * @author tyran8x
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CoreConfigServiceImpl implements CoreConfigService {
	
	private final CoreConfigDefinitionService coreConfigDefinitionService;
	private final CoreConfigValueService coreConfigValueService;
	private final ObjectMapper objectMapper;
	
	@Override
	@Transactional(readOnly = true)
	public Optional<String> getString(String key, Map<ConfigScope, String> scopeChain) {
		return getValue(key, scopeChain, String.class);
	}
	
	private <T> Optional<T> getValue(String key, Map<ConfigScope, String> scopeChain, Class<T> targetType) {
		String appCode = scopeChain.get(ConfigScope.APP);
		Optional<CoreConfigDefinition> defOpt = coreConfigDefinitionService.findByKeyAndAppCode(key, appCode);
		
		if (defOpt.isEmpty()) {
			return Optional.empty();
		}
		CoreConfigDefinition def = defOpt.get();
		
		// Lặp qua chuỗi scope theo thứ tự ưu tiên
		for (ConfigScope scope : ConfigScope.getPriorityOrder()) {
			if (scopeChain.containsKey(scope)) {
				String scopeValue = scopeChain.get(scope);
				Optional<CoreConfigValue> valueOpt = coreConfigValueService.findActiveValue(def.getId(), scope.name(), scopeValue);
				if (valueOpt.isPresent()) {
					String rawValue = valueOpt.get().getValue();
					return Optional.of(processAndConvertValue(rawValue, def, targetType));
				}
			}
		}
		
		return Optional.of(processAndConvertValue(def.getDefaultValue(), def, targetType));
	}
	
	private <T> T processAndConvertValue(String rawValue, CoreConfigDefinition definition, Class<T> targetType) {
		String processedValue = rawValue;
		if (Boolean.TRUE.equals(definition.getIsEncrypted())) {
			processedValue = EncryptUtils.encryptByBase64(rawValue);
		}
		
		try {
			if (targetType.isAssignableFrom(String.class)) {
				return targetType.cast(processedValue);
			}
			if (targetType.isAssignableFrom(Integer.class)) {
				return targetType.cast(Integer.valueOf(processedValue));
			}
			if (targetType.isAssignableFrom(Boolean.class)) {
				return targetType.cast(Boolean.valueOf(processedValue));
			}
			return objectMapper.readValue(processedValue, targetType);
		} catch (Exception e) {
			log.error("Không thể chuyển đổi giá trị '{}' sang kiểu {} cho key '{}'. Lỗi: {}", rawValue, targetType.getSimpleName(),
					definition.getKey(), e.getMessage());
			return null; // Trả về null nếu chuyển đổi thất bại
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<Integer> getInteger(String key, Map<ConfigScope, String> scopeChain) {
		return getValue(key, scopeChain, Integer.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<Boolean> getBoolean(String key, Map<ConfigScope, String> scopeChain) {
		return getValue(key, scopeChain, Boolean.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public <T> Optional<T> getObject(String key, Map<ConfigScope, String> scopeChain, Class<T> targetType) {
		return getValue(key, scopeChain, targetType);
	}
	
	@Override
	@Transactional
	public void setValue(String appCode, String key, ConfigScope scope, String scopeValue, Object value) {
		// 1. Tìm định nghĩa cấu hình
		CoreConfigDefinition definition = coreConfigDefinitionService.findByKeyAndAppCode(key, appCode)
				.orElseThrow(() -> new InvalidEntityException(
						String.format("Không tìm thấy định nghĩa cấu hình với key='%s' trong app='%s'", key, appCode)));
		
		// 2. Chuyển đổi giá trị thành String/JSON
		String valueToStore;
		try {
			if (value instanceof String) {
				valueToStore = (String) value;
			} else {
				valueToStore = objectMapper.writeValueAsString(value);
			}
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Không thể serialize giá trị thành JSON.", e);
		}
		
		// 3. Mã hóa nếu cần
		if (Boolean.TRUE.equals(definition.getIsEncrypted())) {
			valueToStore = EncryptUtils.encryptByBase64(valueToStore);
			log.debug("Mã hóa giá trị cho key '{}'", key);
		}
		
		// 4. Gọi service cấp thấp để upsert
		coreConfigValueService.upsertValue(definition.getId(), scope.name(), scopeValue, valueToStore);
		log.info("Đã cập nhật cấu hình key='{}', scope='{}', scopeValue='{}'", key, scope, scopeValue);
		
		// 5. Cân nhắc xóa cache tại đây
		//clearCache(key, scopeChain);
	}
}
