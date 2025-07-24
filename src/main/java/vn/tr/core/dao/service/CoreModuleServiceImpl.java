package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreModule;
import vn.tr.core.data.criteria.CoreModuleSearchCriteria;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
	public List<CoreModule> findAllByIds(Collection<Long> ids) {
		if (ids.isEmpty()) {
			return Collections.emptyList();
		}
		return coreModuleRepo.findAllById(ids);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Collection<Long> ids) {
		if (!ids.isEmpty()) {
			coreModuleRepo.softDeleteByIds(ids);
		}
	}
	
	@Override
	public Optional<CoreModule> findByCodeAndAppCodeIncludingDeleted(String code, String appCode) {
		List<CoreModule> results = coreModuleRepo.findAllByCodeAndAppCodeIncludingDeletedSorted(code, appCode);
		if (results.isEmpty()) return Optional.empty();
		if (results.size() > 1) {
			log.warn(
					"CẢNH BÁO DỮ LIỆU TRÙNG LẶP: Tìm thấy {} bản ghi CoreModule cho code='{}' và appCode='{}'. Hệ thống sẽ tự động chọn bản ghi ưu tiên (ID={}).",
					results.size(), code, appCode, results.getFirst().getId());
		}
		return Optional.of(results.getFirst());
	}
	
	@Override
	public JpaRepository<CoreModule, Long> getRepository() {
		return this.coreModuleRepo;
	}
	
	@Override
	public List<CoreModule> findAllByAppCode(String appCode) {
		return coreModuleRepo.findAllByAppCode(appCode);
	}
	
	@Override
	public List<CoreModule> saveAll(Iterable<CoreModule> modules) {
		return coreModuleRepo.saveAll(modules);
	}
	
}
