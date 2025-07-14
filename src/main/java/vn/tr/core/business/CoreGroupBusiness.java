package vn.tr.core.business;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.core.dao.model.CoreGroup;
import vn.tr.core.dao.service.CoreGroupService;
import vn.tr.core.data.CoreGroupData;
import vn.tr.core.data.criteria.CoreGroupSearchCriteria;
import vn.tr.core.mapper.CoreGroupMapper;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CoreGroupBusiness {
	
	private final CoreGroupService coreGroupService;
	private final CoreGroupMapper coreGroupMapper;
	
	public CoreGroupData create(CoreGroupData coreGroupData) {
		CoreGroup coreGroup = new CoreGroup();
		return save(coreGroup, coreGroupData);
	}
	
	public void delete(Long id) {
		CoreGroup coreGroup = coreGroupService.findById(id).orElseThrow(() -> new EntityNotFoundException(CoreGroup.class, id));
		coreGroup.setDeletedAt(LocalDateTime.now());
		coreGroupService.save(coreGroup);
	}
	
	@Transactional(readOnly = true)
	public Page<CoreGroupData> findAll(CoreGroupSearchCriteria criteria) {
		Pageable pageable = CoreUtils.getPageRequest(criteria.getPage(), criteria.getSize(), criteria.getSortBy(), criteria.getSortDir());
		Page<CoreGroup> pageCoreGroup = coreGroupService.findAll(criteria, pageable);
		
		Set<Long> parentIds = pageCoreGroup.getContent().stream()
				.map(CoreGroup::getParentId)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
		
		Map<Long, CoreGroup> parentGroupMap = coreGroupService.findMapByIds(parentIds);
		
		return pageCoreGroup.map(entity -> {
			CoreGroupData data = coreGroupMapper.toData(entity);
			
			if (entity.getParentId() != null) {
				CoreGroup parentEntity = parentGroupMap.get(entity.getParentId());
				if (parentEntity != null) {
					data.setParent(coreGroupMapper.toBaseData(parentEntity));
				}
			}
			return data;
		});
	}
	
	@Transactional(readOnly = true)
	public CoreGroupData findById(Long id) {
		return coreGroupService.findById(id)
				.map(coreGroupMapper::toData)
				.orElseThrow(() -> new EntityNotFoundException(CoreGroup.class, id));
	}
	
	private CoreGroupData save(CoreGroup coreGroup, CoreGroupData coreGroupData) {
		coreGroupMapper.updateEntityFromData(coreGroupData, coreGroup);
		coreGroup.setDeletedAt(null);
		coreGroup = coreGroupService.save(coreGroup);
		return coreGroupMapper.toData(coreGroup);
	}
	
	public CoreGroupData update(Long id, CoreGroupData coreGroupData) {
		CoreGroup coreGroup = coreGroupService.findById(id).orElseThrow(() -> new EntityNotFoundException(CoreGroup.class, id));
		return save(coreGroup, coreGroupData);
	}
}
