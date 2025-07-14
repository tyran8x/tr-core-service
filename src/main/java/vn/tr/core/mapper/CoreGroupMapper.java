package vn.tr.core.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import vn.tr.common.core.domain.data.BaseCatalogData;
import vn.tr.common.core.enums.CatalogStatus;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.core.utils.FunctionUtils;
import vn.tr.core.dao.model.CoreGroup;
import vn.tr.core.dao.service.CoreGroupService;
import vn.tr.core.data.CoreGroupData;

@Mapper(componentModel = "spring", uses = {CoreGroupService.class})
public abstract class CoreGroupMapper {
	
	private final CoreGroupService coreGroupService;
	
	public CoreGroupMapper(CoreGroupService coreGroupService) {
		this.coreGroupService = coreGroupService;
	}
	
	public abstract CoreGroupData toData(CoreGroup entity);
	
	public abstract CoreGroup toEntity(CoreGroupData data);
	
	public void updateEntityFromData(CoreGroupData data, @MappingTarget CoreGroup entity) {
		if (data == null || entity == null) {
			return;
		}
		
		// Vệ sinh dữ liệu để ngăn chặn XSS
		entity.setCode(FunctionUtils.removeXss(data.getCode()));
		entity.setName(FunctionUtils.removeXss(data.getName()));
		entity.setDescription(FunctionUtils.removeXss(data.getDescription()));
		
		// Kiểm tra và gán parentId
		if (data.getParentId() != null) {
			coreGroupService.findById(data.getParentId())
					.orElseThrow(() -> new EntityNotFoundException(CoreGroup.class, data.getParentId()));
			entity.setParentId(data.getParentId());
		} else {
			entity.setParentId(null);
		}
		
		// Gán các trường khác
		entity.setSortOrder(data.getSortOrder());
		entity.setStatus(data.getStatus());
		entity.setAppId(data.getAppId());
	}
	
	public abstract BaseCatalogData toBaseData(CoreGroup entity);
	
	@AfterMapping
	protected void enrichData(CoreGroup entity, @MappingTarget BaseCatalogData data) {
		if (entity == null || data == null) {
			return;
		}
		data.setStatusName(CatalogStatus.getLabelByValue(entity.getStatus()));
	}
	
	@AfterMapping
	protected void enrichCoreGroupData(CoreGroup entity, @MappingTarget CoreGroupData data) {
		if (entity == null || data == null || entity.getParentId() == null) {
			return;
		}
		
		coreGroupService.findById(entity.getParentId()).ifPresent(parentEntity -> {
			BaseCatalogData parentData = toBaseData(parentEntity);
			data.setParent(parentData);
		});
	}
}
