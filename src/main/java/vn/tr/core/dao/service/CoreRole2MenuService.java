package vn.tr.core.dao.service;

import vn.tr.core.dao.model.CoreRole2Menu;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CoreRole2MenuService {

	void deleteById(Long id);

	boolean existsById(Long id);

	List<CoreRole2Menu> findByDaXoaFalse();

	Optional<CoreRole2Menu> findById(Long id);

	List<CoreRole2Menu> findByMenuIdAndDaXoaFalse(Long menuId);

	List<CoreRole2Menu> findByRoleIdAndDaXoaFalse(Long roleId);

	Optional<CoreRole2Menu> findFirstByRoleIdAndMenuId(Long roleId, Long menuId);

	List<Long> getMenuIds(Set<String> roles);

	Set<String> getMenuMas(Set<String> roles);

	CoreRole2Menu save(CoreRole2Menu coreRole2Menu);

	void setFixedDaXoaForRoleId(boolean daXoa, Long roleId);

	void refreshRolePermsCache();

	void refreshRolePermsCache(String roleMa);

	void refreshRolePermsCache(String oldRoleMa, String newRoleMa);

}
