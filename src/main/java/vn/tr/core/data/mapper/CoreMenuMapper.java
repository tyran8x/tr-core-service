package vn.tr.core.data.mapper;

import org.mapstruct.*;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.core.dao.model.CoreMenu;
import vn.tr.core.data.dto.CoreMenuData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CoreMenuMapper {
	
	CoreMenuData toData(CoreMenu entity);
	
	@Mapping(target = "id", ignore = true)
	CoreMenu toEntity(CoreMenuData data);
	
	default void save(CoreMenuData data, CoreMenu entity) {
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
	void updateEntity(CoreMenuData data, @MappingTarget CoreMenu entity);
	
}
