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
	
	private final CoreModuleRepo coreModuleRepo;
	
	@Override
	public Optional<CoreModule> findById(Long id) {
		return coreModuleRepo.findById(id);
	}
	
	@Override
	public CoreModule save(CoreModule coreModule) {
		return coreModuleRepo.save(coreModule);
	}
	
	@Override
	public void delete(Long id) {
		coreModuleRepo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return coreModuleRepo.existsById(id);
	}
	
	@Override
	public Page<CoreModule> findAll(CoreModuleSearchCriteria coreModuleSearchCriteria, Pageable pageable) {
		return coreModuleRepo.findAll(CoreModuleSpecifications.quickSearch(coreModuleSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreModule> findAll(CoreModuleSearchCriteria coreModuleSearchCriteria) {
		return coreModuleRepo.findAll(CoreModuleSpecifications.quickSearch(coreModuleSearchCriteria));
	}
	
	@Override
	public boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode) {
		return coreModuleRepo.existsByIdNotAndCodeIgnoreCaseAndAppCode(id, code, appCode);
	}
	
	@Override
	public boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode) {
		return coreModuleRepo.existsByIdNotAndNameIgnoreCaseAndAppCode(id, name, appCode);
	}
	
	@Override
	public boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode) {
		return coreModuleRepo.existsByCodeIgnoreCaseAndAppCode(code, appCode);
	}
	
	@Override
	public boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode) {
		return coreModuleRepo.existsByNameIgnoreCaseAndAppCode(name, appCode);
	}
	
	@Override
	public boolean existsByIdAndAppCode(long id, String appCode) {
		return coreModuleRepo.existsByIdAndAppCode(id, appCode);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Set<Long> ids) {
		if (ids.isEmpty()) {
			return;
		}
		coreModuleRepo.softDeleteByIds(ids);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Set<String> findAllCodesByAppCode(String appCode) {
		return coreModuleRepo.findAllCodesByAppCode(appCode);
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
					return coreModuleRepo.save(newModule);
				});
	}
	
	@Transactional(readOnly = true)
	public Optional<CoreModule> findByCodeSafely(String appCode, String code) {
		List<CoreModule> foundModules = coreModuleRepo.findByAppCodeIgnoreCaseAndCodeIgnoreCase(appCode, code);
		
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
		return coreModuleRepo.findAllByAppCode(appCode);
	}
	
}
