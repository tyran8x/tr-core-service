package vn.tr.core.data.mapper;

import org.mapstruct.*;
import vn.tr.core.dao.model.CoreClient;
import vn.tr.core.data.dto.CoreClientData;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CoreClientMapper {
	
	CoreClientData toData(CoreClient coreClient);
	
	List<CoreClientData> toData(List<CoreClient> coreClients);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	CoreClient toEntity(CoreClientData coreClientData);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "appCode", ignore = true) // Luôn luôn ignore ở đây
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	void updateEntityFromData(CoreClientData coreClientData, @MappingTarget CoreClient coreClient);
	
}
