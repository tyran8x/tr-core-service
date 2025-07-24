package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreConfigDefinition;
import vn.tr.core.data.criteria.CoreConfigDefinitionSearchCriteria;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Lớp triển khai cho CoreConfigDefinitionService.
 *
 * @author tyran8x
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CoreConfigDefinitionServiceImpl implements CoreConfigDefinitionService {
	
	private final CoreConfigDefinitionRepo coreConfigDefinitionRepo;
	
	@Override
	public Optional<CoreConfigDefinition> findById(Long id) {
		return coreConfigDefinitionRepo.findById(id);
	}
	
	@Override
	public List<CoreConfigDefinition> findAllByIds(Collection<Long> ids) {
		if (ids.isEmpty()) {
			return Collections.emptyList();
		}
		return coreConfigDefinitionRepo.findAllById(ids);
	}
	
	@Override
	public CoreConfigDefinition save(CoreConfigDefinition definition) {
		return coreConfigDefinitionRepo.save(definition);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Collection<Long> ids) {
		if (!ids.isEmpty()) {
			coreConfigDefinitionRepo.softDeleteByIds(ids);
		}
	}
	
	@Override
	public Page<CoreConfigDefinition> findAll(CoreConfigDefinitionSearchCriteria criteria, Pageable pageable) {
		return coreConfigDefinitionRepo.findAll(CoreConfigDefinitionSpecifications.quickSearch(criteria), pageable);
	}
	
	@Override
	public List<CoreConfigDefinition> findAll(CoreConfigDefinitionSearchCriteria criteria) {
		return coreConfigDefinitionRepo.findAll(CoreConfigDefinitionSpecifications.quickSearch(criteria));
	}
	
	@Override
	public Optional<CoreConfigDefinition> findByKeyAndAppCode(String key, String appCode) {
		return coreConfigDefinitionRepo.findByKeyAndAppCode(key, appCode);
	}
	
	@Override
	public Optional<CoreConfigDefinition> findByKeyAndAppCodeIncludingDeleted(String key, String appCode) {
		List<CoreConfigDefinition> results = coreConfigDefinitionRepo.findByKeyAndAppCodeIncludingDeletedSorted(key, appCode);
		if (results.isEmpty()) return Optional.empty();
		if (results.size() > 1) {
			log.warn(
					"CẢNH BÁO DỮ LIỆU TRÙNG LẶP: Tìm thấy {} bản ghi CoreConfigDefinition cho key='{}' và appCode='{}'. Hệ thống sẽ tự động chọn bản ghi ưu tiên (ID={}).",
					results.size(), key, appCode, results.getFirst().getId());
		}
		return Optional.of(results.getFirst());
	}
	
	@Override
	public boolean existsByKeyIgnoreCaseAndAppCode(String key, String appCode) {
		return coreConfigDefinitionRepo.existsByKeyIgnoreCaseAndAppCode(key, appCode);
	}
	
	@Override
	public boolean existsByIdNotAndKeyIgnoreCaseAndAppCode(long id, String key, String appCode) {
		return coreConfigDefinitionRepo.existsByIdNotAndKeyIgnoreCaseAndAppCode(id, key, appCode);
	}
	
	@Override
	public JpaRepository<CoreConfigDefinition, Long> getRepository() {
		return this.coreConfigDefinitionRepo;
	}
}
