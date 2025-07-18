package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreModule;
import vn.tr.core.data.criteria.CoreModuleSearchCriteria;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreModuleServiceImpl implements CoreModuleService {
	
	private final CoreModuleRepo repo;
	
	@Override
	public Optional<CoreModule> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public CoreModule save(CoreModule coreModule) {
		return repo.save(coreModule);
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
	public Page<CoreModule> findAll(CoreModuleSearchCriteria coreModuleSearchCriteria, Pageable pageable) {
		return repo.findAll(CoreModuleSpecifications.quickSearch(coreModuleSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreModule> findAll(CoreModuleSearchCriteria coreModuleSearchCriteria) {
		return repo.findAll(CoreModuleSpecifications.quickSearch(coreModuleSearchCriteria));
	}
	
	@Override
	public boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode) {
		return repo.existsByIdNotAndCodeIgnoreCaseAndAppCode(id, code, appCode);
	}
	
	@Override
	public boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode) {
		return repo.existsByIdNotAndNameIgnoreCaseAndAppCode(id, name, appCode);
	}
	
	@Override
	public boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode) {
		return repo.existsByCodeIgnoreCaseAndAppCode(code, appCode);
	}
	
	@Override
	public boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode) {
		return repo.existsByNameIgnoreCaseAndAppCode(name, appCode);
	}
	
	@Override
	public boolean existsByIdAndAppCode(long id, String appCode) {
		return repo.existsByIdAndAppCode(id, appCode);
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
	@Transactional(readOnly = true)
	public Set<String> findAllCodesByAppCode(String appCode) {
		return repo.findAllCodesByAppCode(appCode);
	}
	
	@Override
	@Transactional
	public CoreModule findOrCreate(String code, String name, String appCode) {
		String normalizedCode = code.toLowerCase();
		String normalizedAppCode = appCode.toLowerCase();
		
		return findByCodeSafely(normalizedAppCode, normalizedCode)
				.orElseGet(() -> {
					CoreModule newModule = new CoreModule();
					newModule.setAppCode(normalizedAppCode);
					newModule.setCode(normalizedCode);
					newModule.setName(name);
					return repo.save(newModule);
				});
	}
	
	@Transactional(readOnly = true)
	public Optional<CoreModule> findByCodeSafely(String appCode, String code) {
		List<CoreModule> foundModules = repo.findByAppCodeIgnoreCaseAndCodeIgnoreCase(appCode, code);
		
		if (foundModules.isEmpty()) {
			return Optional.empty();
		}
		
		// Ưu tiên bản ghi khớp chính xác cả kiểu chữ
		Optional<CoreModule> exactMatch = foundModules.stream()
				.filter(module -> module.getAppCode().equals(appCode) && module.getCode().equals(code))
				.findFirst();
		
		if (exactMatch.isPresent()) {
			return exactMatch;
		}
		
		if (foundModules.size() > 1) {
			log.warn("Cảnh báo dữ liệu không nhất quán: Tìm thấy {} bản ghi cho module code '{}' trong app '{}'. " +
							"Hệ thống sẽ ưu tiên bản ghi có ID nhỏ nhất.",
					foundModules.size(), code, appCode);
			return foundModules.stream().min(Comparator.comparing(CoreModule::getId));
		}
		
		return Optional.of(foundModules.getFirst());
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CoreModule> findAllByAppCode(String appCode) {
		return repo.findAllByAppCode(appCode);
	}
	
}
