package vn.tr.core.data.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.tr.core.dao.model.CoreMenu;
import vn.tr.core.data.dto.CoreMenuData;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Mapper(
		componentModel = "spring",
		unmappedTargetPolicy = ReportingPolicy.IGNORE,
		uses = {ObjectMapper.class}
)
public interface CoreMenuMapper {
	
	Logger log = LoggerFactory.getLogger(CoreMenuMapper.class);
	
	CoreMenuData toData(CoreMenu entity);
	
	List<CoreMenuData> toData(List<CoreMenu> coreMenus);
	
	@Mapping(target = "id", ignore = true)
	CoreMenu toEntity(CoreMenuData data);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "appCode", ignore = true) // Luôn luôn ignore ở đây
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	void updateEntityFromData(CoreMenuData data, @MappingTarget CoreMenu entity);
	
	default Map<String, Object> mapExtraMetaFromJsonString(String jsonString) {
		if (jsonString == null || jsonString.isBlank()) {
			return Collections.emptyMap();
		}
		try {
			// Sử dụng một ObjectMapper để parse chuỗi JSON
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(jsonString, new TypeReference<>() {
			});
		} catch (JsonProcessingException e) {
			log.error("Lỗi khi deserialize chuỗi JSON của extra_meta: {}", jsonString, e);
			// Trả về map rỗng hoặc ném ra một exception tùy theo yêu cầu nghiệp vụ
			return Collections.emptyMap();
		}
	}
	
	default String mapExtraMetaToJsonString(Map<String, Object> map) {
		if (map == null || map.isEmpty()) {
			return "{}"; // Hoặc trả về null tùy thuộc vào CSDL
		}
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			log.error("Lỗi khi serialize extra_meta Map thành JSON: {}", map, e);
			return "{}";
		}
	}
	
}
