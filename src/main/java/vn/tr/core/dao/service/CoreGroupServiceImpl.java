package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreGroup;
import vn.tr.core.data.criteria.CoreGroupSearchCriteria;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Lớp triển khai cho CoreGroupService, với logic xử lý dữ liệu trùng lặp một cách an toàn.
 *
 * @author tyran8x
 * @version 2.3 (Forgiving Strategy)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CoreGroupServiceImpl implements CoreGroupService {
	
	private final CoreGroupRepo coreGroupRepo;
	
	@Override
	public Optional<CoreGroup> findById(Long id) {
		return coreGroupRepo.findById(id);
	}
	
	@Override
	public List<CoreGroup> findAllByIds(Collection<Long> ids) {
		if (ids.isEmpty()) {
			return Collections.emptyList();
		}
		return coreGroupRepo.findAllById(ids);
	}
	
	@Override
	public CoreGroup save(CoreGroup coreGroup) {
		return coreGroupRepo.save(coreGroup);
	}
	
	@Override
	public void delete(Long id) {
		coreGroupRepo.deleteById(id);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Collection<Long> ids) {
		if (ids.isEmpty()) {
			return;
		}
		coreGroupRepo.softDeleteByIds(ids);
	}
	
	@Override
	public Page<CoreGroup> findAll(CoreGroupSearchCriteria coreGroupSearchCriteria, Pageable pageable) {
		return coreGroupRepo.findAll(CoreGroupSpecifications.quickSearch(coreGroupSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreGroup> findAll(CoreGroupSearchCriteria coreGroupSearchCriteria) {
		return coreGroupRepo.findAll(CoreGroupSpecifications.quickSearch(coreGroupSearchCriteria));
	}
	
	@Override
	public List<CoreGroup> findAllByAppCodeAndCodeIn(String appCode, Collection<String> codes) {
		if (codes.isEmpty()) {
			return Collections.emptyList();
		}
		return coreGroupRepo.findAllByAppCodeAndCodeIn(appCode, codes);
	}
	
	/**
	 * Tìm một nhóm theo code và appCode, bao gồm cả đã xóa mềm.
	 * Phương thức này được thiết kế để xử lý dữ liệu trùng lặp một cách uyển chuyển.
	 * Nó sẽ ưu tiên trả về bản ghi đang hoạt động và được cập nhật gần nhất,
	 * đồng thời ghi log cảnh báo nếu phát hiện trùng lặp.
	 *
	 * @return Optional.empty() nếu không tìm thấy.
	 *
	 * @return Optional.of(group) nếu tìm thấy một hoặc nhiều bản ghi (sẽ chọn bản ghi ưu tiên).
	 */
	@Override
	public Optional<CoreGroup> findByCodeAndAppCodeIncludingDeleted(String code, String appCode) {
		List<CoreGroup> results = coreGroupRepo.findAllByCodeAndAppCodeIncludingDeletedSorted(code, appCode);
		
		if (results.isEmpty()) {
			return Optional.empty();
		}
		
		if (results.size() > 1) {
			log.warn("CẢNH BÁO DỮ LIỆU TRÙNG LẶP: Tìm thấy {} bản ghi CoreGroup cho code='{}' và appCode='{}'. " +
							"Hệ thống sẽ tự động chọn bản ghi ưu tiên (ID={}) để tiếp tục xử lý.",
					results.size(), code, appCode, results.getFirst().getId());
		}
		
		// Luôn trả về phần tử đầu tiên, vì nó đã được sắp xếp theo quy tắc ưu tiên.
		return Optional.of(results.getFirst());
	}
	
	@Override
	public JpaRepository<CoreGroup, Long> getRepository() {
		return this.coreGroupRepo;
	}
	
	// --- Forwarding methods for validation ---
	
	@Override
	public boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode) {
		return coreGroupRepo.existsByIdNotAndCodeIgnoreCaseAndAppCode(id, code, appCode);
	}
	
	@Override
	public boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode) {
		return coreGroupRepo.existsByIdNotAndNameIgnoreCaseAndAppCode(id, name, appCode);
	}
	
	@Override
	public boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode) {
		return coreGroupRepo.existsByCodeIgnoreCaseAndAppCode(code, appCode);
	}
	
	@Override
	public boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode) {
		return coreGroupRepo.existsByNameIgnoreCaseAndAppCode(name, appCode);
	}
	
	@Override
	public boolean existsByIdAndAppCode(long id, String appCode) {
		return coreGroupRepo.existsByIdAndAppCode(id, appCode);
	}
}
