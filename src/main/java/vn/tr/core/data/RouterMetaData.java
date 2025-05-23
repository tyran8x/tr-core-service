package vn.tr.core.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouterMetaData {
	
	/**
	 * Tiêu đề của route, thường được sử dụng cho document.title hoặc breadcrumbs. Ví dụ: "Dashboard", "User Profile"
	 */
	private String title;
	
	/**
	 * Route này có yêu cầu xác thực không. Ví dụ: true (cần đăng nhập), false (công khai)
	 */
	private Boolean requiresAuth;
	
	/**
	 * Các vai trò được phép truy cập route này. Ví dụ: ["ADMIN", "EDITOR"]
	 */
	private List<String> roles;
	
	/**
	 * Các quyền cụ thể cần để truy cập route này. Ví dụ: ["user:create", "user:edit"]
	 */
	private List<String> permissions;
	
	/**
	 * Icon cho route, thường dùng trong menu. Ví dụ: "mdi-home", "el-icon-user"
	 */
	private String icon;
	
	/**
	 * Có ẩn route này khỏi menu không. Ví dụ: true (ẩn), false (hiển thị)
	 */
	private Boolean hideInMenu;
	
	/**
	 * Route cha sẽ được highlight trong menu khi route con này active. Hữu ích khi menu không hiển thị route con. Ví dụ: "/management/users" (khi
	 * đang ở /management/users/edit)
	 */
	private String activeMenu;
	
	/**
	 * Có cache trang này bằng <keep-alive> không. Ví dụ: true (cache), false (không cache)
	 */
	private Boolean noCache;
	
	/**
	 * Tên của layout component sẽ được sử dụng cho route này. Ví dụ: "AdminLayout", "PublicLayout", "AuthLayout"
	 */
	private String layout;
	
	/**
	 * Bất kỳ thuộc tính meta tùy chỉnh nào khác không được liệt kê ở trên. Giúp DTO linh hoạt hơn.
	 */
	private Map<String, Object> extra;
	
	private String activeIcon;
	private String activePath;
	private Boolean affixTab;
	private Integer affixTabOrder;
	private Set<String> authority;
	private String badge;
	private String badgeType;
	private String badgeVariants;
	private Boolean hideChildrenInMenu;
	private Boolean hideInBreadcrumb;
	private Boolean hideInTab;
	private String iframeSrc;
	private Boolean ignoreAccess;
	private Boolean keepAlive;
	private String link;
	private Boolean loaded;
	private Integer maxNumOfOpenTab;
	private Boolean menuVisibleWithForbidden;
	private Boolean noBasicLayout;
	private Boolean openInNewWindow;
	private Integer order;
	private String query;
}
