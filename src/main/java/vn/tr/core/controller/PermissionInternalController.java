package vn.tr.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.service.PermissionService;

import java.util.Set;

@RestController
@RequestMapping("/internal/permissions")
@RequiredArgsConstructor
public class PermissionInternalController {
	
	private final PermissionService permissionService;
	
	// Dùng POST để body có thể chứa Set<String>
	@PostMapping("/codes/{userId}")
	public Set<String> getPermissionCodes(@PathVariable Long userId, @RequestBody Set<String> appCodes) {
		return permissionService.getPermissionCodes(userId, appCodes);
	}
	
	@PostMapping("/roles/{userId}")
	public Set<String> getRoleCodes(@PathVariable Long userId, @RequestBody Set<String> appCodes) {
		return permissionService.getRoleCodes(userId, appCodes);
	}
	
	@PostMapping("/groups/{userId}")
	public Set<String> getGroupCodes(@PathVariable Long userId, @RequestBody Set<String> appCodes) {
		return permissionService.getGroupCodes(userId, appCodes);
	}
}
