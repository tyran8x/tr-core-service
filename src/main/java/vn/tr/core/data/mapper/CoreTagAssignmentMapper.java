package vn.tr.core.data.mapper;

import org.mapstruct.*;
import vn.tr.core.dao.model.CoreTagAssignment;
import vn.tr.core.data.dto.CoreTagAssignmentData;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CoreTagAssignmentMapper {
	
	CoreTagAssignmentData toData(CoreTagAssignment coreTagAssignment);
	
	List<CoreTagAssignmentData> toData(List<CoreTagAssignment> coreTagAssignments);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	CoreTagAssignment toEntity(CoreTagAssignmentData coreTagAssignmentData);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	void updateEntityFromData(CoreTagAssignmentData coreTagAssignmentData, @MappingTarget CoreTagAssignment coreTagAssignment);
}
