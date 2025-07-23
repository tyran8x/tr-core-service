package vn.tr.core.data.mapper;

import org.mapstruct.*;
import vn.tr.core.dao.model.CorePermission;
import vn.tr.core.data.dto.CorePermissionData;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CorePermissionMapper {
	
	CorePermissionData toData(CorePermission corePermission);
	
	List<CorePermissionData> toData(List<CorePermission> corePermissions);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	CorePermission toEntity(CorePermissionData corePermissionData);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "appCode", ignore = true) // Luôn luôn ignore ở đây
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	void updateEntityFromData(CorePermissionData corePermissionData, @MappingTarget CorePermission corePermission);
	
}
