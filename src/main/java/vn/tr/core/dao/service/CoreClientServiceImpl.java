package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreClient;
import vn.tr.core.data.criteria.CoreClientSearchCriteria;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreClientServiceImpl implements CoreClientService {
	
	private final CoreClientRepo coreClientRepo;
	
	@Override
	public Optional<CoreClient> findById(Long id) {
		return coreClientRepo.findById(id);
	}
	
	@Override
	public List<CoreClient> findAllByIds(Collection<Long> ids) {
		if (ids.isEmpty()) {
			return Collections.emptyList();
		}
		return coreClientRepo.findAllById(ids);
	}
	
	@Override
	public CoreClient save(CoreClient coreClient) {
		return coreClientRepo.save(coreClient);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Collection<Long> ids) {
		if (!ids.isEmpty()) {
			coreClientRepo.softDeleteByIds(ids);
		}
	}
	
	@Override
	public Page<CoreClient> findAll(CoreClientSearchCriteria criteria, Pageable pageable) {
		return coreClientRepo.findAll(CoreClientSpecifications.quickSearch(criteria), pageable);
	}
	
	@Override
	public Optional<CoreClient> findByClientIdAndAppCodeIncludingDeleted(String clientId, String appCode) {
		List<CoreClient> results = coreClientRepo.findByClientIdAndAppCodeIncludingDeletedSorted(clientId, appCode);
		if (results.isEmpty()) return Optional.empty();
		if (results.size() > 1) {
			log.warn(
					"CẢNH BÁO DỮ LIỆU TRÙNG LẶP: Tìm thấy {} bản ghi CoreClient cho clientId='{}' và appCode='{}'. Hệ thống sẽ tự động chọn bản ghi ưu tiên (ID={}).",
					results.size(), clientId, appCode, results.getFirst().getId());
		}
		return Optional.of(results.getFirst());
	}
	
	@Override
	public boolean existsByIdNotAndClientIdAndAppCode(Long id, String clientId, String appCode) {
		return coreClientRepo.existsByIdNotAndClientIdAndAppCode(id, clientId, appCode);
	}
	
	@Override
	public boolean existsByClientIdAndAppCode(String clientId, String appCode) {
		return coreClientRepo.existsByClientIdAndAppCode(clientId, appCode);
	}
	
	@Override
	public Optional<CoreClient> findByClientId(String clientId) {
		return coreClientRepo.findByClientId(clientId);
	}
	
	@Override
	public JpaRepository<CoreClient, Long> getRepository() {
		return this.coreClientRepo;
	}
	
}
