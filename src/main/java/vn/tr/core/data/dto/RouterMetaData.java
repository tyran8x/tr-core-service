package vn.tr.core.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RouterMetaData {
	
	private String title;
	private String icon;
	private Boolean keepAlive;
	private Integer order;
	
	// Các thuộc tính phổ biến khác
	private Boolean hideInMenu;
	private String activeMenu;
	private String layout;
	
	// Các thuộc tính chuyên biệt
	private String activeIcon;
	private String activePath;
	private Boolean affixTab;
	private Integer affixTabOrder;
	private String badge;
	private String badgeType;
	private String badgeVariants;
	private Boolean hideChildrenInMenu;
	private Boolean hideInBreadcrumb;
	private Boolean hideInTab;
	private String iframeSrc;
	private Boolean ignoreAccess;
	private String link;
	private Boolean loaded;
	private Integer maxNumOfOpenTab;
	private Boolean menuVisibleWithForbidden;
	private Boolean noBasicLayout;
	private Boolean noCache;
	private Boolean openInNewWindow;
	private String query;
	
	// Các thuộc tính liên quan đến phân quyền (có thể FE không cần gửi lên,
	// nhưng để đây để đầy đủ)
	private Boolean requiresAuth;
	private Set<String> roles;
	private Set<String> permissions;
	private Set<String> authority;
	
	// Thuộc tính để chứa các giá trị không xác định trước
	private Map<String, Object> extra;
}
