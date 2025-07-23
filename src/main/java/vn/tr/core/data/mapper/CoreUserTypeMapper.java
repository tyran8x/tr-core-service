package vn.tr.core.data.mapper;

import org.mapstruct.*;
import vn.tr.core.dao.model.CoreUserType;
import vn.tr.core.data.dto.CoreUserTypeData;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CoreUserTypeMapper {
	
	CoreUserTypeData toData(CoreUserType coreUserType);
	
	List<CoreUserTypeData> toData(List<CoreUserType> coreUserTypes);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	CoreUserType toEntity(CoreUserTypeData coreUserTypeData);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "appCode", ignore = true) // Luôn luôn ignore ở đây
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	void updateEntityFromData(CoreUserTypeData coreUserTypeData, @MappingTarget CoreUserType coreUserType);
	
}
