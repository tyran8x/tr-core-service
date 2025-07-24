package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreMenu;
import vn.tr.core.data.criteria.CoreMenuSearchCriteria;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreMenuServiceImpl implements CoreMenuService {
	
	private final CoreMenuRepo coreMenuRepo;
	
	@Override
	public void delete(Long id) {
		coreMenuRepo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return coreMenuRepo.existsById(id);
	}
	
	@Override
	public Page<CoreMenu> findAll(CoreMenuSearchCriteria coreMenuSearchCriteria, Pageable pageable) {
		return coreMenuRepo.findAll(CoreMenuSpecifications.quickSearch(coreMenuSearchCriteria), pageable);
	}
	
	@Override
	public List<CoreMenu> findAll(CoreMenuSearchCriteria coreMenuSearchCriteria) {
		return coreMenuRepo.findAll(CoreMenuSpecifications.quickSearch(coreMenuSearchCriteria));
	}
	
	@Override
	public Optional<CoreMenu> findById(Long id) {
		return coreMenuRepo.findById(id);
	}
	
	@Override
	public CoreMenu save(CoreMenu coreMenu) {
		return coreMenuRepo.save(coreMenu);
	}
	
	@Override
	@Transactional
	public void deleteByIds(Collection<Long> ids) {
		if (ids.isEmpty()) {
			return;
		}
		coreMenuRepo.softDeleteByIds(ids);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<CoreMenu> findByCodeSafely(String appCode, String code) {
		// Tìm kiếm không phân biệt hoa thường
		List<CoreMenu> foundMenus = coreMenuRepo.findByAppCodeIgnoreCaseAndCodeIgnoreCase(appCode, code);
		
		if (foundMenus.isEmpty()) {
			return Optional.empty(); // Không tìm thấy gì
		}
		
		// Cố gắng tìm một bản ghi khớp chính xác cả kiểu chữ (ưu tiên cao nhất)
		Optional<CoreMenu> exactMatch = foundMenus.stream()
				.filter(menu -> menu.getAppCode().equals(appCode) && menu.getCode().equals(code))
				.findFirst();
		
		if (exactMatch.isPresent()) {
			// Nếu có một bản ghi khớp hoàn toàn, luôn ưu tiên nó
			return exactMatch;
		}
		
		// Nếu không có bản ghi nào khớp chính xác, nhưng lại tìm thấy nhiều hơn 1 bản ghi
		// khi tìm kiếm không phân biệt hoa thường (ví dụ: 'userlist', 'USERLIST')
		if (foundMenus.size() > 1) {
			log.warn("Cảnh báo dữ liệu không nhất quán: Tìm thấy {} bản ghi cho menu code '{}' trong app '{}' " +
							"khi tìm kiếm không phân biệt hoa thường, nhưng không có bản ghi nào khớp chính xác. " +
							"Hệ thống sẽ ưu tiên bản ghi có ID nhỏ nhất.",
					foundMenus.size(), code, appCode);
			
			// Quy tắc nhất quán: chọn bản ghi được tạo ra đầu tiên (ID nhỏ nhất)
			return foundMenus.stream().min(Comparator.comparing(CoreMenu::getId));
		}
		
		// Trường hợp cuối: Chỉ có 1 bản ghi được tìm thấy (ví dụ: tìm 'UserList' ra 'userlist')
		return Optional.of(foundMenus.getFirst());
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CoreMenu> findAllByAppCodeIncludingDeleted(String appCode) {
		return coreMenuRepo.findAllByAppCodeIncludingDeleted(appCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CoreMenu> findAllByAppCode(String appCode) {
		return coreMenuRepo.findAllByAppCode(appCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean hasChildren(Long menuId) {
		return coreMenuRepo.existsByParentId(menuId);
	}
	
	@Override
	public void saveAll(Iterable<CoreMenu> menus) {
		coreMenuRepo.saveAll(menus);
	}
	
	@Override
	@Transactional
	public void softDeleteAll(List<CoreMenu> menusToDelete) {
		if (menusToDelete.isEmpty()) {
			return;
		}
		List<Long> idsToDelete = menusToDelete.stream()
				.map(CoreMenu::getId)
				.collect(Collectors.toList());
		coreMenuRepo.softDeleteByIds(idsToDelete);
	}
	
}
