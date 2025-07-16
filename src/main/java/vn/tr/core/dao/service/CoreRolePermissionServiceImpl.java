package vn.tr.core.dao.service;

import cn.hutool.core.collection.CollectionUtil;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.redis.utils.RedisUtils;
import vn.tr.core.dao.model.CoreRole;
import vn.tr.core.dao.model.CoreRolePermission;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class CoreRolePermissionServiceImpl implements CoreRolePermissionService {
	
	private final CoreRolePermissionRepo repo;
	
	private final CoreRoleService coreRoleService;
	
	@PostConstruct
	public void initRolePermsCache() {
		log.info("initRolePermsCache... ");
		refreshRolePermsCache();
	}
	
	@Override
	public void deleteById(Long id) {
		repo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}
	
	@Override
	public List<CoreRolePermission> findByDaXoaFalse() {
		return repo.findByDaXoaFalse();
	}
	
	@Override
	public Optional<CoreRolePermission> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public List<CoreRolePermission> findByMenuIdAndDaXoaFalse(Long menuId) {
		return repo.findByMenuIdAndDaXoaFalse(menuId);
	}
	
	@Override
	public List<CoreRolePermission> findByRoleIdAndDaXoaFalse(Long roleId) {
		return repo.findByRoleIdAndDaXoaFalse(roleId);
	}
	
	@Override
	public Optional<CoreRolePermission> findFirstByRoleIdAndMenuId(Long roleId, Long menuId) {
		return repo.findFirstByRoleIdAndMenuId(roleId, menuId);
	}
	
	@Override
	public Set<String> getMenuMas(Set<String> roles) {
		return repo.getMenuMas(roles);
	}
	
	@Override
	public CoreRolePermission save(CoreRolePermission coreRole2Menu) {
		return repo.save(coreRole2Menu);
	}
	
	@Override
	public void setFixedDaXoaForRoleCode(boolean daXoa, String roleCode) {
		repo.setFixedDaXoaForRoleCode(daXoa, roleCode);
	}
	
	@Override
	public void refreshRolePermsCache() {
		RedisUtils.deleteKeys(CoreRolePermission.class.getSimpleName() + "*");
		
		List<CoreRole> coreRoles = coreRoleService.findByTrangThaiTrueAndDaXoaFalse();
		if (CollectionUtil.isNotEmpty(coreRoles)) {
			coreRoles.forEach(item -> {
				String roleCode = item.getCode();
				Set<String> perms = repo.getMenuMas(Collections.singleton(roleCode));
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
		
		Optional<CoreRole> optionalCoreRole = coreRoleService.findFirstByMaIgnoreCaseAndDaXoaFalse(roleCode);
		if (optionalCoreRole.isPresent()) {
			Set<String> perms = repo.getMenuMas(Collections.singleton(roleCode));
			if (CollectionUtil.isNotEmpty(perms)) {
				RedisUtils.setCacheObject(CoreRolePermission.class.getSimpleName() + ":" + roleCode, perms);
			}
		}
	}
	
	@Override
	public void refreshRolePermsCache(String oldRoleCode, String newRoleCode) {
		
		RedisUtils.deleteObject(CoreRolePermission.class.getSimpleName() + ":" + oldRoleCode);
		
		Optional<CoreRole> optionalCoreRole = coreRoleService.findFirstByMaIgnoreCaseAndDaXoaFalse(newRoleCode);
		if (optionalCoreRole.isPresent()) {
			Set<String> perms = repo.getMenuMas(Collections.singleton(newRoleCode));
			if (CollectionUtil.isNotEmpty(perms)) {
				RedisUtils.setCacheObject(CoreRolePermission.class.getSimpleName() + ":" + newRoleCode, perms);
			}
		}
	}
	
}
