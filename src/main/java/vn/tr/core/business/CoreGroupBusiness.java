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
import vn.tr.core.dao.model.CoreGroup;
import vn.tr.core.dao.service.CoreGroupService;
import vn.tr.core.data.criteria.CoreGroupSearchCriteria;
import vn.tr.core.data.dto.CoreGroupData;
import vn.tr.core.data.mapper.CoreGroupMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CoreGroupBusiness {
	
	private final CoreGroupService coreGroupService;
	private final CoreGroupMapper coreGroupMapper;
	
	public CoreGroupData create(CoreGroupData coreGroupData) {
		CoreGroup coreGroup = coreGroupMapper.toEntity(coreGroupData);
		coreGroup.setAppCode(LoginHelper.getAppCode());
		return save(coreGroup, coreGroupData);
	}
	
	private CoreGroupData save(CoreGroup coreGroup, CoreGroupData coreGroupData) {
		coreGroupMapper.save(coreGroupData, coreGroup);
		if (coreGroupData.getParentId() != null) {
			CoreGroup parentEntity = coreGroupService.findById(coreGroupData.getParentId())
					.orElseThrow(() -> new EntityNotFoundException(CoreGroup.class, coreGroupData.getParentId()));
			
			if (Objects.equals(LifecycleStatus.INACTIVE, parentEntity.getStatus())) {
				throw new IllegalStateException("Không thể gán vào nhóm cha đã bị vô hiệu hóa.");
			}
			coreGroup.setParentId(coreGroupData.getParentId());
		} else {
			coreGroup.setParentId(null);
		}
		CoreGroup savedGroup = coreGroupService.save(coreGroup);
		return findById(savedGroup.getId());
	}
	
	@Transactional(readOnly = true)
	public CoreGroupData findById(Long id) {
		return coreGroupService.findById(id)
				.map(coreGroupMapper::toData)
				.orElseThrow(() -> new EntityNotFoundException(CoreGroup.class, id));
	}
	
	public void delete(Long id) {
		if (!coreGroupService.existsById(id)) {
			throw new EntityNotFoundException(CoreGroup.class, id);
		}
		coreGroupService.delete(id);
	}
	
	public void bulkDelete(Set<Long> ids) {
		coreGroupService.deleteByIds(ids);
	}
	
	@Transactional(readOnly = true)
	public Page<CoreGroupData> findAll(CoreGroupSearchCriteria criteria) {
		Pageable pageable = CoreUtils.getPageRequest(criteria.getPage(), criteria.getSize(), criteria.getSortBy(), criteria.getSortDir());
		Page<CoreGroup> pageCoreGroup = coreGroupService.findAll(criteria, pageable);
		return pageCoreGroup.map(coreGroupMapper::toData);
	}
	
	@Transactional(readOnly = true)
	public List<CoreGroupData> getAll(CoreGroupSearchCriteria criteria) {
		List<CoreGroup> pageCoreGroup = coreGroupService.findAll(criteria);
		return pageCoreGroup.stream().map(coreGroupMapper::toData).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public Optional<CoreGroupData> getById(Long id) {
		if (id == null) {
			return Optional.empty();
		}
		return coreGroupService.findById(id).map(coreGroupMapper::toData);
	}
	
	public CoreGroupData update(Long id, CoreGroupData coreGroupData) {
		CoreGroup coreGroup = coreGroupService.findById(id).orElseThrow(() -> new EntityNotFoundException(CoreGroup.class, id));
		return save(coreGroup, coreGroupData);
	}
}
