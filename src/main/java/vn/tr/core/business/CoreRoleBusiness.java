package vn.tr.core.business;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.core.dao.model.CoreRole;
import vn.tr.core.dao.service.CoreRoleService;
import vn.tr.core.data.criteria.CoreRoleSearchCriteria;
import vn.tr.core.data.dto.CoreRoleData;
import vn.tr.core.data.mapper.CoreRoleMapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CoreRoleBusiness {
	
	private final CoreRoleService coreRoleService;
	private final CoreRoleMapper coreRoleMapper;
	
	public CoreRoleData create(CoreRoleData coreRoleData) {
		CoreRole coreRole = coreRoleMapper.toEntity(coreRoleData);
		coreRole.setAppCode(LoginHelper.getAppCode());
		return save(coreRole, coreRoleData);
	}
	
	private CoreRoleData save(CoreRole coreRole, CoreRoleData coreRoleData) {
		coreRoleMapper.save(coreRoleData, coreRole);
		CoreRole savedRole = coreRoleService.save(coreRole);
		return findById(savedRole.getId());
	}
	
	@Transactional(readOnly = true)
	public CoreRoleData findById(Long id) {
		return coreRoleService.findById(id)
				.map(coreRoleMapper::toData)
				.orElseThrow(() -> new EntityNotFoundException(CoreRole.class, id));
	}
	
	public void delete(Long id) {
		if (!coreRoleService.existsById(id)) {
			throw new EntityNotFoundException(CoreRole.class, id);
		}
		coreRoleService.delete(id);
	}
	
	public void bulkDelete(Set<Long> ids) {
		coreRoleService.deleteByIds(ids);
	}
	
	@Transactional(readOnly = true)
	public Page<CoreRoleData> findAll(CoreRoleSearchCriteria criteria) {
		Pageable pageable = CoreUtils.getPageRequest(criteria.getPage(), criteria.getSize(), criteria.getSortBy(), criteria.getSortDir());
		Page<CoreRole> pageCoreRole = coreRoleService.findAll(criteria, pageable);
		return pageCoreRole.map(coreRoleMapper::toData);
	}
	
	@Transactional(readOnly = true)
	public List<CoreRoleData> getAll(CoreRoleSearchCriteria criteria) {
		List<CoreRole> pageCoreRole = coreRoleService.findAll(criteria);
		return pageCoreRole.stream().map(coreRoleMapper::toData).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public Optional<CoreRoleData> getById(Long id) {
		if (id == null) {
			return Optional.empty();
		}
		return coreRoleService.findById(id).map(coreRoleMapper::toData);
	}
	
	public CoreRoleData update(Long id, CoreRoleData coreRoleData) {
		CoreRole coreRole = coreRoleService.findById(id).orElseThrow(() -> new EntityNotFoundException(CoreRole.class, id));
		return save(coreRole, coreRoleData);
	}
}
