package vn.tr.core.data.mapper;

import org.mapstruct.*;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.core.dao.model.CoreModule;
import vn.tr.core.data.dto.CoreModuleData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CoreModuleMapper {
	
	CoreModuleData toData(CoreModule entity);
	
	@Mapping(target = "id", ignore = true)
	CoreModule toEntity(CoreModuleData data);
	
	default void save(CoreModuleData data, CoreModule entity) {
		updateEntity(data, entity);
		
		if (entity.getStatus() == null) {
			entity.setStatus(LifecycleStatus.ACTIVE);
		}
		
		if (entity.getAppCode() == null && data.getAppCode() != null) {
			entity.setAppCode(data.getAppCode());
		}
	}
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "appCode", ignore = true) // Luôn luôn ignore ở đây
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	void updateEntity(CoreModuleData data, @MappingTarget CoreModule entity);
	
}
