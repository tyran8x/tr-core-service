package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreApp;
import vn.tr.core.data.criteria.CoreAppSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CoreAppServiceImpl implements CoreAppService {
	
	private final CoreAppRepo coreAppRepo;
	
	@Override
	public Optional<CoreApp> findById(Long id) {
		return coreAppRepo.findById(id);
	}
	
	@Override
	public CoreApp save(CoreApp coreApp) {
		return coreAppRepo.save(coreApp);
	}
	
	@Override
	public void delete(Long id) {
		coreAppRepo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return coreAppRepo.existsById(id);
	}
	
	@Override
	public Page<CoreApp> findAll(CoreAppSearchCriteria coreAppSearchCriteria, Pageable pageable) {
		return coreAppRepo.findAll(CoreAppSpecifications.quickSearch(coreAppSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreApp> findAll(CoreAppSearchCriteria coreAppSearchCriteria) {
		return coreAppRepo.findAll(CoreAppSpecifications.quickSearch(coreAppSearchCriteria));
	}
	
	@Override
	public boolean existsByIdNotAndCodeIgnoreCase(long id, String code) {
		return coreAppRepo.existsByIdNotAndCodeIgnoreCase(id, code);
	}
	
	@Override
	public boolean existsByIdNotAndNameIgnoreCase(long id, String name) {
		return coreAppRepo.existsByIdNotAndNameIgnoreCase(id, name);
	}
	
	@Override
	public boolean existsByCodeIgnoreCase(String code) {
		return coreAppRepo.existsByCodeIgnoreCase(code);
	}
	
	@Override
	public boolean existsByNameIgnoreCase(String name) {
		return coreAppRepo.existsByNameIgnoreCase(name);
	}
	
	@Override
	public boolean existsById(long id) {
		return coreAppRepo.existsById(id);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Set<Long> ids) {
		if (ids.isEmpty()) {
			return;
		}
		coreAppRepo.softDeleteByIds(ids);
	}
	
	@Override
	@Transactional
	public CoreApp findOrCreate(String code, String name) {
		return coreAppRepo.findFirstByCodeIgnoreCase(code)
				.orElseGet(() -> {
					CoreApp newApp = new CoreApp();
					newApp.setCode(code);
					newApp.setName(name);
					return coreAppRepo.save(newApp);
				});
	}
	
}
