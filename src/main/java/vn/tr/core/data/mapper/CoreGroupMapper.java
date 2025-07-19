package vn.tr.core.data.mapper;

import org.mapstruct.*;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.common.web.data.dto.BaseData;
import vn.tr.core.dao.model.CoreGroup;
import vn.tr.core.data.dto.CoreGroupData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CoreGroupMapper {
	
	@Mapping(source = "parent", target = "parent")
	CoreGroupData toData(CoreGroup entity);
	
	BaseData toBaseData(CoreGroup entity);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "parent", ignore = true)
	CoreGroup toEntity(CoreGroupData data);
	
	default void save(CoreGroupData data, CoreGroup entity) {
		updateEntityFromData(data, entity);
		
		if (entity.getStatus() == null) {
			entity.setStatus(LifecycleStatus.ACTIVE);
		}
		
		if (entity.getAppCode() == null && data.getAppCode() != null) {
			entity.setAppCode(data.getAppCode());
		}
	}
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "parent", ignore = true)
	@Mapping(target = "appCode", ignore = true) // Luôn luôn ignore ở đây
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	void updateEntityFromData(CoreGroupData data, @MappingTarget CoreGroup entity);
	
}
