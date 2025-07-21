package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.common.core.enums.UserType;
import vn.tr.common.core.exception.ServiceException;
import vn.tr.common.jpa.helper.AssociationSyncHelper;
import vn.tr.core.dao.model.CoreUserApp;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreUserAppServiceImpl implements CoreUserAppService {
	
	private final CoreUserAppRepo coreUserAppRepo;
	private final AssociationSyncHelper associationSyncHelper;
	
	@Override
	public void deleteById(Long id) {
		coreUserAppRepo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return coreUserAppRepo.existsById(id);
	}
	
	@Override
	public Optional<CoreUserApp> findById(Long id) {
		return coreUserAppRepo.findById(id);
	}
	
	@Override
	public CoreUserApp save(CoreUserApp coreUserApp) {
		return coreUserAppRepo.save(coreUserApp);
	}
	
	public void synchronizeUserApps(String username, Set<String> newAppCodes) {
		// 1. Chuẩn bị ngữ cảnh "chủ thể"
		var ownerContext = new CoreUserAppContext(username);
		
		// 2. Lấy tất cả các bản ghi liên quan (kể cả đã xóa mềm)
		List<CoreUserApp> existingAssignments = coreUserAppRepo.findAllByUsernameIncludingDeleted(ownerContext.username());
		
		// 3. Gọi Helper để thực hiện toàn bộ logic đồng bộ hóa
		associationSyncHelper.synchronize(
				ownerContext,
				existingAssignments,
				newAppCodes,
				CoreUserApp::getAppCode,
				() -> CoreUserApp.builder().userTypeCode(UserType.INTERNAL.getUserType()).build(), // Tạo mới với userType mặc định
				(association, context) -> association.setUsername(context.username()),
				CoreUserApp::setAppCode,
				coreUserAppRepo);
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
	@Transactional(readOnly = true)
	public Set<String> findActiveAppCodesByUsername(String username) {
		if (username.isBlank()) {
			return Collections.emptySet();
		}
		return coreUserAppRepo.findActiveAppCodesByUsername(username.toLowerCase());
	}
	
	@Override
	@Transactional
	public void assignUserToApp(String username, String appCode, String defaultUserType) {
		// Sử dụng phương thức truy vấn cả bản ghi đã xóa mềm để xử lý trường hợp kích hoạt lại
		Optional<CoreUserApp> existingAssignment = coreUserAppRepo
				.findAllByUsernameAndAppCodeIncludingDeleted(username, appCode)
				.stream()
				.findFirst();
		
		if (existingAssignment.isPresent()) {
			// Trường hợp 1: Đã có bản ghi -> Kiểm tra xem nó có đang bị xóa mềm không
			CoreUserApp assignment = existingAssignment.get();
			if (assignment.getDeletedAt() != null) {
				log.info("Kích hoạt lại quyền truy cập app '{}' cho người dùng '{}'", appCode, username);
				assignment.setDeletedAt(null);
				// Có thể cập nhật lại status hoặc các trường khác nếu cần
				// assignment.setStatus(LifecycleStatus.ACTIVE);
				coreUserAppRepo.save(assignment);
			}
			// Nếu không bị xóa mềm (deletedAt == null), không cần làm gì cả.
		} else {
			// Trường hợp 2: Chưa có bản ghi nào -> Tạo mới hoàn toàn
			log.info("Gán mới quyền truy cập app '{}' cho người dùng '{}'", appCode, username);
			CoreUserApp newAssignment = CoreUserApp.builder()
					.username(username)
					.appCode(appCode)
					.userTypeCode(defaultUserType)
					// .status(LifecycleStatus.ACTIVE) // Builder đã có giá trị mặc định
					.build();
			coreUserAppRepo.save(newAssignment);
		}
	}
	
	private record CoreUserAppContext(String username) {
	}
	
}
