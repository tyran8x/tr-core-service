package vn.tr.core.dao.service;

import cn.hutool.core.collection.CollectionUtil;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.common.redis.utils.RedisUtils;
import vn.tr.core.dao.model.CorePermission;
import vn.tr.core.dao.model.CoreRole;
import vn.tr.core.dao.model.CoreRolePermission;
import vn.tr.core.data.criteria.CoreRoleSearchCriteria;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class CoreRolePermissionServiceImpl implements CoreRolePermissionService {
	
	private final CoreRolePermissionRepo coreRolePermissionRepo;
	
	private final CoreRoleService coreRoleService;
	
	@PostConstruct
	public void initRolePermsCache() {
		log.info("initRolePermsCache... ");
		refreshRolePermsCache();
	}
	
	@Override
	public void deleteById(Long id) {
		coreRolePermissionRepo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return coreRolePermissionRepo.existsById(id);
	}
	
	@Override
	public Optional<CoreRolePermission> findById(Long id) {
		return coreRolePermissionRepo.findById(id);
	}
	
	@Override
	public CoreRolePermission save(CoreRolePermission coreRole2Menu) {
		return coreRolePermissionRepo.save(coreRole2Menu);
	}
	
	@Override
	public void refreshRolePermsCache() {
		RedisUtils.deleteKeys(CoreRolePermission.class.getSimpleName() + "*");
		
		CoreRoleSearchCriteria coreRoleSearchCriteria = new CoreRoleSearchCriteria();
		coreRoleSearchCriteria.setStatus(LifecycleStatus.ACTIVE);
		List<CoreRole> coreRoles = coreRoleService.findAll(coreRoleSearchCriteria);
		if (CollectionUtil.isNotEmpty(coreRoles)) {
			coreRoles.forEach(item -> {
				String roleCode = item.getCode();
				Set<String> perms = null;// repo.getMenuMas(Collections.singleton(roleCode));
				log.info("refreshRolePermsCache: {}, {}", roleCode, perms);
				if (CollectionUtil.isNotEmpty(perms)) {
					RedisUtils.setCacheObject(CoreRolePermission.class.getSimpleName() + ":" + roleCode, perms);
				}
			});
		}
	}
	
	@Override
	public void refreshRolePermsCache(String roleCode) {
		RedisUtils.deleteObject(CoreRolePermission.class.getSimpleName() + ":" + roleCode);

//		if (coreRoleService.existsByCodeIgnoreCase(roleCode)) {
//			Set<String> perms = null;// repo.getMenuMas(Collections.singleton(roleCode));
//			if (CollectionUtil.isNotEmpty(perms)) {
//				RedisUtils.setCacheObject(CoreRolePermission.class.getSimpleName() + ":" + roleCode, perms);
//			}
//		}
	}
	
	@Override
	public void refreshRolePermsCache(String oldRoleCode, String newRoleCode) {
		
		RedisUtils.deleteObject(CoreRolePermission.class.getSimpleName() + ":" + oldRoleCode);

//		if (coreRoleService.existsByCodeIgnoreCase(newRoleCode)) {
//			Set<String> perms = null;// repo.getMenuMas(Collections.singleton(newRoleCode));
//			if (CollectionUtil.isNotEmpty(perms)) {
//				RedisUtils.setCacheObject(CoreRolePermission.class.getSimpleName() + ":" + newRoleCode, perms);
//			}
//		}
	}
	
	@Override
	public List<CoreRolePermission> findByRoleCodeAndAppCodeIncludingDeleted(String roleCode, String appCode) {
		return coreRolePermissionRepo.findAllByRoleCodeAndAppCodeIncludingDeleted(roleCode, appCode);
	}
	
	@Override
	public JpaRepository<CoreRolePermission, Long> getRepository() {
		return this.coreRolePermissionRepo;
	}
	
	@Override
	public Set<String> findPermissionCodesByRoleCodesAndAppCode(Collection<String> roleCodes, String appCode) {
		return coreRolePermissionRepo.findPermissionCodesByRoleCodesAndAppCode(roleCodes, appCode);
	}
	
	@Override
	public boolean isRoleInUse(CoreRole role) {
		return coreRolePermissionRepo.existsByRoleCodeAndAppCode(role.getCode(), role.getAppCode());
	}
	
	@Override
	public boolean isPermissionInUse(CorePermission permission) {
		return coreRolePermissionRepo.existsByPermissionCodeAndAppCode(permission.getCode(), permission.getAppCode());
	}
	
	@Override
	public Map<String, Set<String>> findActivePermissionsForRoles(Set<String> roleCodes, String appCode) {
		if (roleCodes.isEmpty()) {
			return Collections.emptyMap();
		}
		List<CoreRolePermission> assignments = coreRolePermissionRepo.findActiveByRoleCodesAndAppCode(roleCodes, appCode);
		return assignments.stream()
				.collect(Collectors.groupingBy(CoreRolePermission::getRoleCode,
						Collectors.mapping(CoreRolePermission::getPermissionCode, Collectors.toSet())));
	}
	
}
