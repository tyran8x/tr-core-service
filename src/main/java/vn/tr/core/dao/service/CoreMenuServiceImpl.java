package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreMenu;
import vn.tr.core.data.criteria.CoreMenuSearchCriteria;

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
	public Page<CoreMenu> findAll(CoreMenuSearchCriteria coreMenuSearchCriteria, Pageable pageable) {
		return repo.findAll(CoreMenuSpecifications.quickSearch(coreMenuSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreMenu> findAll(CoreMenuSearchCriteria coreMenuSearchCriteria) {
		return repo.findAll(CoreMenuSpecifications.quickSearch(coreMenuSearchCriteria));
	}
	
	@Override
	public Optional<CoreMenu> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public CoreMenu save(CoreMenu coreMenu) {
		return repo.save(coreMenu);
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
	
	@Override
	public Optional<CoreMenu> findFirstByCodeIgnoreCaseAndAppCodeIgnoreCase(String code, String appCode) {
		return repo.findFirstByCodeIgnoreCaseAndAppCodeIgnoreCase(code, appCode);
	}
	
}
