package vn.tr.core.data.mapper;

import org.mapstruct.*;
import vn.tr.common.web.data.dto.BaseData;
import vn.tr.core.dao.model.CoreGroup;
import vn.tr.core.data.dto.CoreGroupData;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CoreGroupMapper {
	
	@Mapping(source = "parent", target = "parent")
	CoreGroupData toData(CoreGroup coreGroup);
	
	List<CoreGroupData> toData(List<CoreGroup> coreGroups);
	
	BaseData toBaseData(CoreGroup coreGroup);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "parent", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	CoreGroup toEntity(CoreGroupData coreGroupData);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "parent", ignore = true)
	@Mapping(target = "appCode", ignore = true) // Luôn luôn ignore ở đây
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	void updateEntityFromData(CoreGroupData coreGroupData, @MappingTarget CoreGroup coreGroup);
	
}
