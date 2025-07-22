package vn.tr.core.data.mapper;

import org.mapstruct.*;
import vn.tr.core.dao.model.CoreTag;
import vn.tr.core.data.dto.CoreTagData;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CoreTagMapper {
	
	CoreTagData toData(CoreTag coreTag);
	
	List<CoreTagData> toData(List<CoreTag> coreTags);
	
	@Mapping(target = "id", ignore = true)
	CoreTag toEntity(CoreTagData coreTagData);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "appCode", ignore = true) // Luôn luôn ignore ở đây
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	void updateEntityFromData(CoreTagData coreTagData, @MappingTarget CoreTag coreTag);
	
}
