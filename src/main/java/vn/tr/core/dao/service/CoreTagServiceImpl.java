package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreTag;
import vn.tr.core.data.criteria.CoreTagSearchCriteria;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreTagServiceImpl implements CoreTagService {
	
	private final CoreTagRepo coreTagRepo;
	
	@Override
	public Optional<CoreTag> findById(Long id) {
		return coreTagRepo.findById(id);
	}
	
	@Override
	public List<CoreTag> findAllByIds(Collection<Long> ids) {
		if (ids.isEmpty()) {
			return Collections.emptyList();
		}
		return coreTagRepo.findAllById(ids);
	}
	
	@Override
	public CoreTag save(CoreTag coreTag) {
		return coreTagRepo.save(coreTag);
	}
	
	@Override
	public void delete(Long id) {
		coreTagRepo.deleteById(id);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Collection<Long> ids) {
		if (!ids.isEmpty()) {
			coreTagRepo.softDeleteByIds(ids);
		}
	}
	
	@Override
	public Page<CoreTag> findAll(CoreTagSearchCriteria coreTagSearchCriteria, Pageable pageable) {
		return coreTagRepo.findAll(CoreTagSpecifications.quickSearch(coreTagSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreTag> findAll(CoreTagSearchCriteria coreTagSearchCriteria) {
		return coreTagRepo.findAll(CoreTagSpecifications.quickSearch(coreTagSearchCriteria));
	}
	
	@Override
	public Optional<CoreTag> findByCodeIgnoreCaseIncludingDeleted(String code) {
		List<CoreTag> results = coreTagRepo.findAllByCodeIgnoreCaseIncludingDeletedSorted(code);
		if (results.isEmpty()) return Optional.empty();
		if (results.size() > 1) {
			log.warn("CẢNH BÁO DỮ LIỆU TRÙNG LẶP: Tìm thấy {} bản ghi CoreTag cho code='{}'. Hệ thống sẽ tự động chọn bản ghi ưu tiên (ID={}).",
					results.size(), code, results.getFirst().getId());
		}
		return Optional.of(results.getFirst());
	}
	
	@Override
	public JpaRepository<CoreTag, Long> getRepository() {
		return this.coreTagRepo;
	}
	
	@Override
	public boolean existsByIdNotAndCodeIgnoreCase(long id, String code) {
		return coreTagRepo.existsByIdNotAndCodeIgnoreCase(id, code);
	}
	
	@Override
	public boolean existsByIdNotAndNameIgnoreCase(long id, String name) {
		return coreTagRepo.existsByIdNotAndNameIgnoreCase(id, name);
	}
	
	@Override
	public boolean existsByCodeIgnoreCase(String code) {
		return coreTagRepo.existsByCodeIgnoreCase(code);
	}
	
	@Override
	public boolean existsByNameIgnoreCase(String name) {
		return coreTagRepo.existsByNameIgnoreCase(name);
	}
	
	@Override
	public boolean existsById(Long id) {
		return coreTagRepo.existsById(id);
	}
	
}
