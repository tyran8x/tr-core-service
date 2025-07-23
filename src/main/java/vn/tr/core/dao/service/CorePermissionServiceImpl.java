package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CorePermission;
import vn.tr.core.data.criteria.CorePermissionSearchCriteria;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CorePermissionServiceImpl implements CorePermissionService {
	
	private final CorePermissionRepo corePermissionRepo;
	
	@Override
	public Optional<CorePermission> findById(Long id) {
		return corePermissionRepo.findById(id);
	}
	
	@Override
	public CorePermission save(CorePermission corePermission) {
		return corePermissionRepo.save(corePermission);
	}
	
	@Override
	public void delete(Long id) {
		corePermissionRepo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return corePermissionRepo.existsById(id);
	}
	
	@Override
	public void saveAll(Iterable<CorePermission> corePermissions) {
		corePermissionRepo.saveAll(corePermissions);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Set<String> findAllCodesByAppCode(String appCode) {
		return corePermissionRepo.findAllCodesByAppCode(appCode);
	}
	
	@Override
	public List<CorePermission> findAllByIds(Collection<Long> ids) {
		if (ids.isEmpty()) {
			return Collections.emptyList();
		}
		return corePermissionRepo.findAllById(ids);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CorePermission> findAllByAppCode(String appCode) {
		return corePermissionRepo.findAllByAppCode(appCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Set<String> findAllCodesByUsernameAndAppCode(String username, String appCode) {
		return corePermissionRepo.findAllCodesByUsernameAndAppCode(username.toLowerCase(), appCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean isSuperAdmin(String username) {
		return corePermissionRepo.isSuperAdmin(username.toLowerCase());
	}
	
	@Override
	@Transactional
	public void deleteByIds(Collection<Long> ids) {
		if (ids.isEmpty()) {
			return;
		}
		corePermissionRepo.softDeleteByIds(ids);
	}
	
	@Override
	public Page<CorePermission> findAll(CorePermissionSearchCriteria criteria, Pageable pageable) {
		return corePermissionRepo.findAll(CorePermissionSpecifications.quickSearch(criteria), pageable);
	}
	
	@Override
	public List<CorePermission> findAll(CorePermissionSearchCriteria criteria) {
		return corePermissionRepo.findAll(CorePermissionSpecifications.quickSearch(criteria));
	}
	
	@Override
	public Optional<CorePermission> findByCodeAndAppCodeIncludingDeleted(String code, String appCode) {
		List<CorePermission> results = corePermissionRepo.findAllByCodeAndAppCodeIncludingDeletedSorted(code, appCode);
		if (results.isEmpty()) return Optional.empty();
		if (results.size() > 1) {
			log.warn(
					"CẢNH BÁO DỮ LIỆU TRÙNG LẶP: Tìm thấy {} bản ghi CorePermission cho code='{}' và appCode='{}'. Hệ thống sẽ tự động chọn bản ghi ưu tiên (ID={}).",
					results.size(), code, appCode, results.getFirst().getId());
		}
		return Optional.of(results.getFirst());
	}
	
	@Override
	public boolean isModuleInUse(Long moduleId) {
		return corePermissionRepo.existsByModuleId(moduleId);
	}
	
	@Override
	public JpaRepository<CorePermission, Long> getRepository() {
		return this.corePermissionRepo;
	}
	
	@Override
	public boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode) {
		return corePermissionRepo.existsByIdNotAndCodeIgnoreCaseAndAppCode(id, code, appCode);
	}
	
	@Override
	public boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode) {
		return corePermissionRepo.existsByIdNotAndNameIgnoreCaseAndAppCode(id, name, appCode);
	}
	
	@Override
	public boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode) {
		return corePermissionRepo.existsByCodeIgnoreCaseAndAppCode(code, appCode);
	}
	
	@Override
	public boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode) {
		return corePermissionRepo.existsByNameIgnoreCaseAndAppCode(name, appCode);
	}
	
	/**
	 * BỔ SUNG: Triển khai logic tìm kiếm.
	 */
	@Override
	public List<CorePermission> findAllByAppCodeAndCodeIn(String appCode, Collection<String> codes) {
		if (codes.isEmpty()) {
			return Collections.emptyList();
		}
		return corePermissionRepo.findAllByAppCodeAndCodeIn(appCode, codes);
	}
	
}
