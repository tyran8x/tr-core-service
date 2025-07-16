package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreMenu;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class CoreMenuServiceImpl implements CoreMenuService {
	
	private final CoreMenuRepo repo;
	
	public CoreMenuServiceImpl(CoreMenuRepo repo) {
		this.repo = repo;
	}
	
	@Override
	public void delete(Long id) {
		repo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}
	
	@Override
	public Page<CoreMenu> findAll(String search, Boolean trangThai, String appCode, Pageable pageable) {
		return repo.findAll(CoreMenuSpecifications.quickSearch(search, trangThai, appCode), pageable);
	}
	
	@Override
	public List<CoreMenu> findByDaXoaFalse() {
		return repo.findByDaXoaFalse();
	}
	
	@Override
	public Optional<CoreMenu> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public Optional<CoreMenu> findByIdAndDaXoaFalse(Long id) {
		return repo.findByIdAndDaXoaFalse(id);
	}
	
	@Override
	public List<CoreMenu> findByIdInAndDaXoaFalse(List<Long> ids) {
		return repo.findByIdInAndDaXoaFalse(ids);
	}
	
	@Override
	public List<CoreMenu> findByIdInAndTrangThaiTrueAndAppCodeAndDaXoaFalse(List<Long> ids, String appCode) {
		return repo.findByIdInAndTrangThaiTrueAndAppCodeAndDaXoaFalse(ids, appCode);
	}
	
	@Override
	public List<CoreMenu> findByTrangThaiTrueAndAppCodeAndDaXoaFalse(String appCode) {
		return repo.findByTrangThaiTrueAndAppCodeAndDaXoaFalse(appCode);
	}
	
	@Override
	public Optional<CoreMenu> findFirstByMaIgnoreCaseAndAppCodeIgnoreCase(String ma, String appCode) {
		return repo.findFirstByMaIgnoreCaseAndAppCodeIgnoreCase(ma, appCode);
	}
	
	@Override
	public CoreMenu save(CoreMenu coreMenu) {
		return repo.save(coreMenu);
	}
	
	@Override
	public void setFixedDaXoa(boolean daXoa) {
		repo.setFixedDaXoa(daXoa);
	}
	
	@Override
	public void setFixedDaXoaAndAppCode(boolean daXoa, String appCode) {
		repo.setFixedDaXoaAndAppCode(daXoa, appCode);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Set<Long> ids) {
		if (ids.isEmpty()) {
			return;
		}
		repo.softDeleteByIds(ids);
	}
	
}
