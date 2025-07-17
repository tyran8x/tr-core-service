package vn.tr.core.data.mapper;

import org.mapstruct.*;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.core.dao.model.CoreContact;
import vn.tr.core.data.dto.CoreContactData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CoreContactMapper {
	
	CoreContactData toData(CoreContact entity);
	
	@Mapping(target = "id", ignore = true)
	CoreContact toEntity(CoreContactData data);
	
	default void save(CoreContactData data, CoreContact entity) {
		_updateEntityFromData(data, entity);
		
		if (entity.getStatus() == null) {
			entity.setStatus(LifecycleStatus.ACTIVE);
		}
		
	}
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	void _updateEntityFromData(CoreContactData data, @MappingTarget CoreContact entity);
	
}
