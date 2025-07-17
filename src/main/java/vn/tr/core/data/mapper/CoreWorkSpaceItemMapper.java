package vn.tr.core.data.mapper;

import org.mapstruct.*;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.common.web.data.dto.BaseData;
import vn.tr.core.dao.model.CoreWorkSpaceItem;
import vn.tr.core.data.dto.CoreWorkSpaceItemData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CoreWorkSpaceItemMapper {
	
	@Mapping(source = "parent", target = "parent")
	CoreWorkSpaceItemData toData(CoreWorkSpaceItem entity);
	
	BaseData toBaseData(CoreWorkSpaceItem entity);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "parent", ignore = true)
	CoreWorkSpaceItem toEntity(CoreWorkSpaceItemData data);
	
	default void save(CoreWorkSpaceItemData data, CoreWorkSpaceItem entity) {
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
	@Mapping(target = "parent", ignore = true)
	@Mapping(target = "appCode", ignore = true) // Luôn luôn ignore ở đây
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	void updateEntity(CoreWorkSpaceItemData data, @MappingTarget CoreWorkSpaceItem entity);
	
}
