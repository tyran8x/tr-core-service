package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreUserApp;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoreUserAppServiceImpl implements CoreUserAppService {
	
	private final CoreUserAppRepo repo;
	
	@Override
	public void deleteById(Long id) {
		repo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}
	
	@Override
	public Optional<CoreUserApp> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public CoreUserApp save(CoreUserApp coreUserApp) {
		return repo.save(coreUserApp);
	}
	
	@Transactional
	public void replaceUserApps(String username, Set<String> newAppCodes) {
		List<CoreUserApp> existingAssignments = repo.findByUsername(username);
		
		// Nếu không có gì thay đổi, thoát sớm để tối ưu
		if (newAppCodes.isEmpty() && existingAssignments.isEmpty()) {
			return;
		}
		
		Set<String> existingAppCodes = existingAssignments.stream()
				.map(CoreUserApp::getAppCode)
				.collect(Collectors.toSet());
		
		// Nếu danh sách mới và cũ giống hệt nhau, không cần làm gì cả
		if (existingAppCodes.equals(newAppCodes)) {
			return;
		}
		
		// Xóa các liên kết không còn trong danh sách mới
		List<CoreUserApp> toDelete = existingAssignments.stream()
				.filter(ua -> !newAppCodes.contains(ua.getAppCode()))
				.toList();
		if (!toDelete.isEmpty()) {
			repo.deleteAllInBatch(toDelete);
		}
		
		// Thêm các liên kết mới
		Set<String> toAdd = newAppCodes.stream()
				.filter(code -> !existingAppCodes.contains(code))
				.collect(Collectors.toSet());
		
		if (!toAdd.isEmpty()) {
			List<CoreUserApp> newAssignments = toAdd.stream()
					.map(appCode -> new CoreUserApp(username, appCode))
					.toList();
			repo.saveAll(newAssignments);
		}
	}
	
}
