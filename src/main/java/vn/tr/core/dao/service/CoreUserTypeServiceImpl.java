package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreUserType;
import vn.tr.core.data.criteria.CoreUserTypeSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CoreUserTypeServiceImpl implements CoreUserTypeService {
	
	private final CoreUserTypeRepo coreUserTypeRepo;
	
	@Override
	public Optional<CoreUserType> findById(Long id) {
		return coreUserTypeRepo.findById(id);
	}
	
	@Override
	public CoreUserType save(CoreUserType coreUserType) {
		return coreUserTypeRepo.save(coreUserType);
	}
	
	@Override
	public void delete(Long id) {
		coreUserTypeRepo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return coreUserTypeRepo.existsById(id);
	}
	
	@Override
	public Page<CoreUserType> findAll(CoreUserTypeSearchCriteria coreUserTypeSearchCriteria, Pageable pageable) {
		return coreUserTypeRepo.findAll(CoreUserTypeSpecifications.quickSearch(coreUserTypeSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreUserType> findAll(CoreUserTypeSearchCriteria coreUserTypeSearchCriteria) {
		return coreUserTypeRepo.findAll(CoreUserTypeSpecifications.quickSearch(coreUserTypeSearchCriteria));
	}
	
	@Override
	public boolean existsByIdNotAndCodeIgnoreCase(long id, String code) {
		return coreUserTypeRepo.existsByIdNotAndCodeIgnoreCase(id, code);
	}
	
	@Override
	public boolean existsByIdNotAndNameIgnoreCase(long id, String name) {
		return coreUserTypeRepo.existsByIdNotAndNameIgnoreCase(id, name);
	}
	
	@Override
	public boolean existsByCodeIgnoreCase(String code) {
		return coreUserTypeRepo.existsByCodeIgnoreCase(code);
	}
	
	@Override
	public boolean existsByNameIgnoreCase(String name) {
		return coreUserTypeRepo.existsByNameIgnoreCase(name);
	}
	
	@Override
	public boolean existsById(long id) {
		return coreUserTypeRepo.existsById(id);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Set<Long> ids) {
		if (ids.isEmpty()) {
			return;
		}
		coreUserTypeRepo.softDeleteByIds(ids);
	}
	
	@Override
	@Transactional
	public CoreUserType findOrCreate(String code, String name) {
		return coreUserTypeRepo.findFirstByCodeIgnoreCase(code)
				.orElseGet(() -> {
					CoreUserType newApp = new CoreUserType();
					newApp.setCode(code);
					newApp.setName(name);
					return coreUserTypeRepo.save(newApp);
				});
	}
	
}
