package vn.tr.core.business;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreModule;
import vn.tr.core.dao.model.CorePermission;
import vn.tr.core.dao.service.CoreModuleService;
import vn.tr.core.dao.service.CorePermissionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CorePermissionBusiness {
	
	private final CorePermissionService corePermissionService;
	private final CoreModuleService coreModuleService;
	
	/**
	 * Lấy cấu trúc cây quyền cho một ứng dụng, được nhóm theo module. Dùng cho giao diện phân quyền vai trò.
	 *
	 * @param appCode
	 * 		Mã của ứng dụng.
	 *
	 * @return Danh sách các module, mỗi module chứa danh sách các quyền của nó.
	 */
	public List<PermissionModuleNode> getPermissionTreeForApp(String appCode) {
		// 1. Lấy tất cả module và permission của app
		List<CoreModule> modules = coreModuleService.findAllByAppCode(appCode);
		List<CorePermission> permissions = corePermissionService.findAllByAppCode(appCode);
		
		// 2. Nhóm các permission theo module_id
		Map<Long, List<PermissionNode>> permissionsByModuleId = permissions.stream()
				.map(this::mapToPermissionNode)
				.collect(Collectors.groupingBy(p -> p.getModuleId() != null ? p.getModuleId() : -1L)); // -1L cho các quyền không có module
		
		// 3. Xây dựng cây kết quả
		List<PermissionModuleNode> tree = modules.stream()
				.map(module -> PermissionModuleNode.builder()
						.moduleCode(module.getCode())
						.moduleName(module.getName())
						.permissions(permissionsByModuleId.getOrDefault(module.getId(), new ArrayList<>()))
						.build())
				.collect(Collectors.toList());
		
		// 4. Thêm các quyền không thuộc module nào vào một nhóm mặc định
		if (permissionsByModuleId.containsKey(-1L)) {
			tree.add(PermissionModuleNode.builder()
					.moduleCode("UNCATEGORIZED")
					.moduleName("Quyền khác")
					.permissions(permissionsByModuleId.get(-1L))
					.build());
		}
		
		return tree;
	}
	
	private PermissionNode mapToPermissionNode(CorePermission permission) {
		return PermissionNode.builder()
				.code(permission.getCode())
				.name(permission.getName())
				.description(permission.getDescription())
				.moduleId(permission.getModuleId())
				.build();
	}
	
	// --- DTOs nội bộ để trả về cho FE ---
	@Data
	@Builder
	public static class PermissionModuleNode {
		private String moduleCode;
		private String moduleName;
		private List<PermissionNode> permissions;
	}
	
	@Data
	@Builder
	public static class PermissionNode {
		private String code;
		private String name;
		private String description;
		private Long moduleId;
	}
}
