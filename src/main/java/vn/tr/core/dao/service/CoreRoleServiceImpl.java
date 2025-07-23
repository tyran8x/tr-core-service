package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreRole;
import vn.tr.core.data.criteria.CoreRoleSearchCriteria;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreRoleServiceImpl implements CoreRoleService {
	
	private final CoreRoleRepo coreRoleRepo;
	
	@Override
	public Optional<CoreRole> findById(Long id) {
		return coreRoleRepo.findById(id);
	}
	
	@Override
	public CoreRole save(CoreRole coreRole) {
		return coreRoleRepo.save(coreRole);
	}
	
	@Override
	public void delete(Long id) {
		coreRoleRepo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return coreRoleRepo.existsById(id);
	}
	
	@Override
	public Page<CoreRole> findAll(CoreRoleSearchCriteria coreRoleSearchCriteria, Pageable pageable) {
		return coreRoleRepo.findAll(CoreRoleSpecifications.quickSearch(coreRoleSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreRole> findAll(CoreRoleSearchCriteria coreRoleSearchCriteria) {
		return coreRoleRepo.findAll(CoreRoleSpecifications.quickSearch(coreRoleSearchCriteria));
	}
	
	@Override
	public boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode) {
		return coreRoleRepo.existsByIdNotAndCodeIgnoreCaseAndAppCode(id, code, appCode);
	}
	
	@Override
	public boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode) {
		return coreRoleRepo.existsByIdNotAndNameIgnoreCaseAndAppCode(id, name, appCode);
	}
	
	@Override
	public boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode) {
		return coreRoleRepo.existsByCodeIgnoreCaseAndAppCode(code, appCode);
	}
	
	@Override
	public boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode) {
		return coreRoleRepo.existsByNameIgnoreCaseAndAppCode(name, appCode);
	}
	
	@Override
	public boolean existsByIdAndAppCode(long id, String appCode) {
		return coreRoleRepo.existsByIdAndAppCode(id, appCode);
	}
	
	@Override
	public List<CoreRole> findAllByIds(Collection<Long> ids) {
		if (ids.isEmpty()) {
			return Collections.emptyList();
		}
		return coreRoleRepo.findAllById(ids);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Collection<Long> ids) {
		if (!ids.isEmpty()) {
			coreRoleRepo.softDeleteByIds(ids);
		}
	}
	
	@Override
	public Optional<CoreRole> findByCodeAndAppCodeIncludingDeleted(String code, String appCode) {
		List<CoreRole> results = coreRoleRepo.findAllByCodeAndAppCodeIncludingDeletedSorted(code, appCode);
		if (results.isEmpty()) return Optional.empty();
		if (results.size() > 1) {
			log.warn(
					"CẢNH BÁO DỮ LIỆU TRÙNG LẶP: Tìm thấy {} bản ghi CoreRole cho code='{}' và appCode='{}'. Hệ thống sẽ tự động chọn bản ghi ưu tiên (ID={}).",
					results.size(), code, appCode, results.getFirst().getId());
		}
		return Optional.of(results.getFirst());
	}
	
	@Override
	public JpaRepository<CoreRole, Long> getRepository() {
		return this.coreRoleRepo;
	}
	
}
