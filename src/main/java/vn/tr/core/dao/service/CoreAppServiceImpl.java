package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreApp;
import vn.tr.core.data.criteria.CoreAppSearchCriteria;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreAppServiceImpl implements CoreAppService {
	
	private final CoreAppRepo coreAppRepo;
	
	@Override
	public Optional<CoreApp> findById(Long id) {
		return coreAppRepo.findById(id);
	}
	
	@Override
	public List<CoreApp> findAllByIds(Collection<Long> ids) {
		if (ids.isEmpty()) {
			return Collections.emptyList();
		}
		return coreAppRepo.findAllById(ids);
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
	@Transactional
	public void deleteByIds(Collection<Long> ids) {
		if (!ids.isEmpty()) {
			coreAppRepo.softDeleteByIds(ids);
		}
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
	public Optional<CoreApp> findByCodeIgnoreCaseIncludingDeleted(String code) {
		List<CoreApp> results = coreAppRepo.findAllByCodeIgnoreCaseIncludingDeletedSorted(code);
		if (results.isEmpty()) return Optional.empty();
		if (results.size() > 1) {
			log.warn("CẢNH BÁO DỮ LIỆU TRÙNG LẶP: Tìm thấy {} bản ghi CoreApp cho code='{}'. Hệ thống sẽ tự động chọn bản ghi ưu tiên (ID={}).",
					results.size(), code, results.getFirst().getId());
		}
		return Optional.of(results.getFirst());
	}
	
	@Override
	public JpaRepository<CoreApp, Long> getRepository() {
		return this.coreAppRepo;
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
	public boolean existsById(Long id) {
		return coreAppRepo.existsById(id);
	}
	
	@Override
	public List<CoreApp> findAllByCodeIn(Collection<String> codes) {
		if (codes.isEmpty()) {
			return Collections.emptyList();
		}
		return coreAppRepo.findAllByCodeIn(codes);
	}
	
}
