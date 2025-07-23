package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vn.tr.common.core.exception.ServiceException;
import vn.tr.core.dao.model.CoreUserApp;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Lớp triển khai cho CoreUserAppService.
 * Chỉ chứa các logic truy vấn và CRUD cơ bản, không chứa nghiệp vụ phức tạp.
 *
 * @author tyran8x
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CoreUserAppServiceImpl implements CoreUserAppService {
	
	private final CoreUserAppRepo coreUserAppRepo;
	
	@Override
	public List<CoreUserApp> findByUsernameIncludingDeleted(String username) {
		return coreUserAppRepo.findAllByUsernameIncludingDeleted(username);
	}
	
	@Override
	public CoreUserApp save(CoreUserApp coreUserApp) {
		return coreUserAppRepo.save(coreUserApp);
	}
	
	@Override
	public void deleteById(Long id) {
		coreUserAppRepo.deleteById(id);
	}
	
	@Override
	public JpaRepository<CoreUserApp, Long> getRepository() {
		return this.coreUserAppRepo;
	}
	
	@Override
	public List<CoreUserApp> findByUsername(String username) {
		return coreUserAppRepo.findByUsername(username);
	}
	
	@Override
	public Set<String> findActiveAppCodesByUsername(String username) {
		if (username.isBlank()) {
			return Collections.emptySet();
		}
		return coreUserAppRepo.findActiveAppCodesByUsername(username);
	}
	
	@Override
	public boolean isUserInApp(String username, String appCode) {
		return coreUserAppRepo.existsByUsernameAndAppCode(username, appCode);
	}
	
	@Override
	public Optional<CoreUserApp> findByUsernameAndAppCode(String username, String appCode) {
		List<CoreUserApp> results = coreUserAppRepo.findByUsernameAndAppCode(username, appCode);
		
		if (results.isEmpty()) {
			return Optional.empty();
		}
		
		if (results.size() > 1) {
			log.error("DỮ LIỆU TRÙNG LẶP NGHIÊM TRỌNG! Tìm thấy {} bản ghi cho username '{}' và appCode '{}'.", results.size(), username, appCode);
			throw new ServiceException("Hệ thống phát hiện dữ liệu không nhất quán. Vui lòng liên hệ quản trị viên.");
		}
		
		return Optional.of(results.getFirst());
	}
	
	@Override
	public Map<String, Set<String>> findActiveAppCodesForUsers(Collection<String> usernames) {
		if (usernames.isEmpty()) {
			return Collections.emptyMap();
		}
		List<CoreUserApp> assignments = coreUserAppRepo.findActiveByUsernamesIn(usernames);
		return assignments.stream()
				.collect(Collectors.groupingBy(
						CoreUserApp::getUsername,
						Collectors.mapping(CoreUserApp::getAppCode, Collectors.toSet())
				                              ));
	}
	
	@Override
	public boolean isAppInUse(String appCode) {
		return coreUserAppRepo.existsByAppCode(appCode);
	}
	
	@Override
	public boolean isUserTypeInUse(String userTypeCode) {
		return coreUserAppRepo.existsByUserTypeCode(userTypeCode);
	}
}
