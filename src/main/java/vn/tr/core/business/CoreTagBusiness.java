package vn.tr.core.business;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.common.web.utils.PagedResult;
import vn.tr.core.dao.model.CoreTag;
import vn.tr.core.dao.service.CoreTagService;
import vn.tr.core.data.criteria.CoreTagSearchCriteria;
import vn.tr.core.data.dto.CoreTagData;
import vn.tr.core.data.mapper.CoreTagMapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CoreTagBusiness {
	
	private final CoreTagService coreTagService;
	private final CoreTagMapper coreTagMapper;
	
	public CoreTagData create(CoreTagData coreTagData) {
		CoreTag coreTag = coreTagMapper.toEntity(coreTagData);
		coreTag.setAppCode(LoginHelper.getAppCode());
		return save(coreTag, coreTagData);
	}
	
	private CoreTagData save(CoreTag coreTag, CoreTagData coreTagData) {
		coreTagMapper.updateEntityFromData(coreTagData, coreTag);
		CoreTag savedTag = coreTagService.save(coreTag);
		return findById(savedTag.getId());
	}
	
	@Transactional(readOnly = true)
	public CoreTagData findById(Long id) {
		return coreTagService.findById(id)
				.map(coreTagMapper::toData)
				.orElseThrow(() -> new EntityNotFoundException(CoreTag.class, id));
	}
	
	public void delete(Long id) {
		if (!coreTagService.existsById(id)) {
			throw new EntityNotFoundException(CoreTag.class, id);
		}
		coreTagService.delete(id);
	}
	
	public void bulkDelete(Set<Long> ids) {
		coreTagService.deleteByIds(ids);
	}
	
	@Transactional(readOnly = true)
	public PagedResult<CoreTagData> findAll(CoreTagSearchCriteria criteria) {
		Pageable pageable = CoreUtils.getPageRequest(criteria.getPage(), criteria.getSize(), criteria.getSortBy(), criteria.getSortDir());
		Page<CoreTag> pageCoreTag = coreTagService.findAll(criteria, pageable);
		return PagedResult.from(pageCoreTag, coreTagMapper::toData);
	}
	
	@Transactional(readOnly = true)
	public List<CoreTagData> getAll(CoreTagSearchCriteria criteria) {
		List<CoreTag> pageCoreTag = coreTagService.findAll(criteria);
		return pageCoreTag.stream().map(coreTagMapper::toData).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public Optional<CoreTagData> getById(Long id) {
		if (id == null) {
			return Optional.empty();
		}
		return coreTagService.findById(id).map(coreTagMapper::toData);
	}
	
	public CoreTagData update(Long id, CoreTagData coreTagData) {
		CoreTag coreTag = coreTagService.findById(id).orElseThrow(() -> new EntityNotFoundException(CoreTag.class, id));
		return save(coreTag, coreTagData);
	}
}
