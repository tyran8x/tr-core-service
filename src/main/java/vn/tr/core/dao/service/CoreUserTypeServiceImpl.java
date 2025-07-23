package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreUserType;
import vn.tr.core.data.criteria.CoreUserTypeSearchCriteria;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
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
	public boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode) {
		return coreUserTypeRepo.existsByIdNotAndCodeIgnoreCaseAndAppCode(id, code, appCode);
	}
	
	@Override
	public boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode) {
		return coreUserTypeRepo.existsByIdNotAndNameIgnoreCaseAndAppCode(id, name, appCode);
	}
	
	@Override
	public boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode) {
		return coreUserTypeRepo.existsByCodeIgnoreCaseAndAppCode(code, appCode);
	}
	
	@Override
	public boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode) {
		return coreUserTypeRepo.existsByNameIgnoreCaseAndAppCode(name, appCode);
	}
	
	@Override
	public boolean existsByIdAndAppCode(long id, String appCode) {
		return coreUserTypeRepo.existsByIdAndAppCode(id, appCode);
	}
	
	@Override
	public List<CoreUserType> findAllByIds(Collection<Long> ids) {
		if (ids.isEmpty()) {
			return Collections.emptyList();
		}
		return coreUserTypeRepo.findAllById(ids);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Collection<Long> ids) {
		if (!ids.isEmpty()) {
			coreUserTypeRepo.softDeleteByIds(ids);
		}
	}
	
	@Override
	public Optional<CoreUserType> findByCodeAndAppCodeIncludingDeleted(String code, String appCode) {
		List<CoreUserType> results = coreUserTypeRepo.findAllByCodeAndAppCodeIncludingDeletedSorted(code, appCode);
		if (results.isEmpty()) return Optional.empty();
		if (results.size() > 1) {
			log.warn(
					"CẢNH BÁO DỮ LIỆU TRÙNG LẶP: Tìm thấy {} bản ghi CoreUserType cho code='{}' và appCode='{}'. Hệ thống sẽ tự động chọn bản ghi ưu tiên (ID={}).",
					results.size(), code, appCode, results.getFirst().getId());
		}
		return Optional.of(results.getFirst());
	}
	
	@Override
	public JpaRepository<CoreUserType, Long> getRepository() {
		return this.coreUserTypeRepo;
	}
	
}
