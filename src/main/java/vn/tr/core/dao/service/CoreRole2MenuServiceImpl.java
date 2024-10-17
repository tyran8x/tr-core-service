package vn.tr.core.dao.service;

import cn.hutool.core.collection.CollectionUtil;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.redis.utils.RedisUtils;
import vn.tr.core.dao.model.CoreRole;
import vn.tr.core.dao.model.CoreRole2Menu;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class CoreRole2MenuServiceImpl implements CoreRole2MenuService {

	private final CoreRole2MenuRepo repo;

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
	public List<CoreRole2Menu> findByDaXoaFalse() {
		return repo.findByDaXoaFalse();
	}

	@Override
	public Optional<CoreRole2Menu> findById(Long id) {
		return repo.findById(id);
	}

	@Override
	public List<CoreRole2Menu> findByMenuIdAndDaXoaFalse(Long menuId) {
		return repo.findByMenuIdAndDaXoaFalse(menuId);
	}

	@Override
	public List<CoreRole2Menu> findByRoleIdAndDaXoaFalse(Long roleId) {
		return repo.findByRoleIdAndDaXoaFalse(roleId);
	}

	@Override
	public Optional<CoreRole2Menu> findFirstByRoleIdAndMenuId(Long roleId, Long menuId) {
		return repo.findFirstByRoleIdAndMenuId(roleId, menuId);
	}

	@Override
	public List<Long> getMenuIds(Set<String> roles) {
		return repo.getMenuIds(roles);
	}

	@Override
	public Set<String> getMenuMas(Set<String> roles) {
		return repo.getMenuMas(roles);
	}

	@Override
	public CoreRole2Menu save(CoreRole2Menu coreRole2Menu) {
		return repo.save(coreRole2Menu);
	}

	@Override
	public void setFixedDaXoaForRoleId(boolean daXoa, Long roleId) {
		repo.setFixedDaXoaForRoleId(daXoa, roleId);
	}

	@Override
	public void refreshRolePermsCache() {
		RedisUtils.deleteKeys(CoreRole2Menu.class.getSimpleName() + "*");

		List<CoreRole> coreRoles = coreRoleService.findByTrangThaiTrueAndDaXoaFalse();
		if (CollectionUtil.isNotEmpty(coreRoles)) {
			coreRoles.forEach(item -> {
				String roleCode = item.getMa();
				Set<String> perms = repo.getMenuMas(Collections.singleton(roleCode));
				log.info("refreshRolePermsCache: {}, {}", roleCode, perms);
				if (CollectionUtil.isNotEmpty(perms)) {
					RedisUtils.setCacheObject(CoreRole2Menu.class.getSimpleName() + ":" + roleCode, perms);
				}
			});
		}
	}

	@Override
	public void refreshRolePermsCache(String roleCode) {
		RedisUtils.deleteObject(CoreRole2Menu.class.getSimpleName() + ":" + roleCode);

		Optional<CoreRole> optionalCoreRole = coreRoleService.findFirstByMaIgnoreCaseAndDaXoaFalse(roleCode);
		if (optionalCoreRole.isPresent()) {
			Set<String> perms = repo.getMenuMas(Collections.singleton(roleCode));
			if (CollectionUtil.isNotEmpty(perms)) {
				RedisUtils.setCacheObject(CoreRole2Menu.class.getSimpleName() + ":" + roleCode, perms);
			}
		}
	}

	@Override
	public void refreshRolePermsCache(String oldRoleCode, String newRoleCode) {

		RedisUtils.deleteObject(CoreRole2Menu.class.getSimpleName() + ":" + oldRoleCode);

		Optional<CoreRole> optionalCoreRole = coreRoleService.findFirstByMaIgnoreCaseAndDaXoaFalse(newRoleCode);
		if (optionalCoreRole.isPresent()) {
			Set<String> perms = repo.getMenuMas(Collections.singleton(newRoleCode));
			if (CollectionUtil.isNotEmpty(perms)) {
				RedisUtils.setCacheObject(CoreRole2Menu.class.getSimpleName() + ":" + newRoleCode, perms);
			}
		}
	}

}
