package vn.tr.core.data.mapper;

import org.mapstruct.*;
import vn.tr.core.dao.model.CoreConfigDefinition;
import vn.tr.core.data.dto.CoreConfigDefinitionData;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CoreConfigDefinitionMapper {
	
	CoreConfigDefinitionData toData(CoreConfigDefinition coreConfigDefinition);
	
	List<CoreConfigDefinitionData> toData(List<CoreConfigDefinition> coreConfigDefinitions);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	CoreConfigDefinition toEntity(CoreConfigDefinitionData coreConfigDefinitionData);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	void updateEntityFromData(CoreConfigDefinitionData coreConfigDefinitionData, @MappingTarget CoreConfigDefinition coreConfigDefinition);
	
}
