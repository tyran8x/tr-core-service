package vn.tr.core.business;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.common.web.utils.PagedResult;
import vn.tr.core.dao.model.CoreWorkSpaceItem;
import vn.tr.core.dao.service.CoreWorkSpaceItemService;
import vn.tr.core.data.criteria.CoreWorkSpaceItemSearchCriteria;
import vn.tr.core.data.dto.CoreWorkSpaceItemData;
import vn.tr.core.data.mapper.CoreWorkSpaceItemMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CoreWorkSpaceItemBusiness {
	
	private final CoreWorkSpaceItemService coreWorkSpaceItemService;
	private final CoreWorkSpaceItemMapper coreWorkSpaceItemMapper;
	
	public CoreWorkSpaceItemData create(CoreWorkSpaceItemData coreWorkSpaceItemData) {
		CoreWorkSpaceItem coreWorkSpaceItem = coreWorkSpaceItemMapper.toEntity(coreWorkSpaceItemData);
		coreWorkSpaceItem.setAppCode(LoginHelper.getAppCode());
		return save(coreWorkSpaceItem, coreWorkSpaceItemData);
	}
	
	private CoreWorkSpaceItemData save(CoreWorkSpaceItem coreWorkSpaceItem, CoreWorkSpaceItemData coreWorkSpaceItemData) {
		coreWorkSpaceItemMapper.updateEntityFromData(coreWorkSpaceItemData, coreWorkSpaceItem);
		if (coreWorkSpaceItemData.getParentId() != null) {
			CoreWorkSpaceItem parentEntity = coreWorkSpaceItemService.findById(coreWorkSpaceItemData.getParentId())
					.orElseThrow(() -> new EntityNotFoundException(CoreWorkSpaceItem.class, coreWorkSpaceItemData.getParentId()));
			
			if (Objects.equals(LifecycleStatus.INACTIVE, parentEntity.getStatus())) {
				throw new IllegalStateException("Không thể gán vào nhóm cha đã bị vô hiệu hóa.");
			}
			coreWorkSpaceItem.setParentId(coreWorkSpaceItemData.getParentId());
		} else {
			coreWorkSpaceItem.setParentId(null);
		}
		CoreWorkSpaceItem savedWorkSpaceItem = coreWorkSpaceItemService.save(coreWorkSpaceItem);
		return findById(savedWorkSpaceItem.getId());
	}
	
	@Transactional(readOnly = true)
	public CoreWorkSpaceItemData findById(Long id) {
		return coreWorkSpaceItemService.findById(id)
				.map(coreWorkSpaceItemMapper::toData)
				.orElseThrow(() -> new EntityNotFoundException(CoreWorkSpaceItem.class, id));
	}
	
	public void delete(Long id) {
		if (!coreWorkSpaceItemService.existsById(id)) {
			throw new EntityNotFoundException(CoreWorkSpaceItem.class, id);
		}
		coreWorkSpaceItemService.delete(id);
	}
	
	public void bulkDelete(Set<Long> ids) {
		coreWorkSpaceItemService.deleteByIds(ids);
	}
	
	@Transactional(readOnly = true)
	public PagedResult<CoreWorkSpaceItemData> findAll(CoreWorkSpaceItemSearchCriteria criteria) {
		Pageable pageable = CoreUtils.getPageRequest(criteria.getPage(), criteria.getSize(), criteria.getSortBy(), criteria.getSortDir());
		Page<CoreWorkSpaceItem> pageCoreWorkSpaceItem = coreWorkSpaceItemService.findAll(criteria, pageable);
		return PagedResult.from(pageCoreWorkSpaceItem, coreWorkSpaceItemMapper::toData);
	}
	
	@Transactional(readOnly = true)
	public List<CoreWorkSpaceItemData> getAll(CoreWorkSpaceItemSearchCriteria criteria) {
		List<CoreWorkSpaceItem> pageCoreWorkSpaceItem = coreWorkSpaceItemService.findAll(criteria);
		return pageCoreWorkSpaceItem.stream().map(coreWorkSpaceItemMapper::toData).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public Optional<CoreWorkSpaceItemData> getById(Long id) {
		if (id == null) {
			return Optional.empty();
		}
		return coreWorkSpaceItemService.findById(id).map(coreWorkSpaceItemMapper::toData);
	}
	
	public CoreWorkSpaceItemData update(Long id, CoreWorkSpaceItemData coreWorkSpaceItemData) {
		CoreWorkSpaceItem coreWorkSpaceItem = coreWorkSpaceItemService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(CoreWorkSpaceItem.class, id));
		return save(coreWorkSpaceItem, coreWorkSpaceItemData);
	}
}
