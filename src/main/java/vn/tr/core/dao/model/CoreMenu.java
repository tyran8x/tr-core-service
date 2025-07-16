package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.tr.common.jpa.entity.BaseCommonEntity;

@Entity
@Table(name = "core_menu")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@SQLDelete(sql = "UPDATE core_menu SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction(value = "deleted_at IS NULL")
public class CoreMenu extends BaseCommonEntity {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "parent_id")
	private Long parentId;
	
	@Column(name = "path")
	private String path;
	
	@Column(name = "component")
	private String component;
	
	@Column(name = "components", columnDefinition = "TEXT")
	private String components;
	
	@Column(name = "alias", columnDefinition = "TEXT")
	private String alias;
	
	@Column(name = "props", columnDefinition = "TEXT")
	private String props;
	
	// Các trường meta
	@Column(name = "redirect")
	private String redirect;
	
	@Column(name = "icon")
	private String icon;
	
	@Column(name = "active_menu")
	private String activeMenu;
	
	@Column(name = "link")
	private String link;
	
	@Column(name = "sapxep")
	private Integer sapXep;
	
	// meta v1
	@Column(name = "is_hidden")
	@ColumnDefault(value = "'false'")
	private Boolean isHidden;
	
	@Column(name = "is_alwaysshow")
	@ColumnDefault(value = "'false'")
	private Boolean isAlwaysShow;
	
	@Column(name = "is_nocache")
	@ColumnDefault(value = "'false'")
	private Boolean isNoCache;
	
	@Column(name = "is_affix")
	@ColumnDefault(value = "'false'")
	private Boolean isAffix;
	
	@Column(name = "is_breadcrumb")
	@ColumnDefault(value = "'false'")
	private Boolean isBreadcrumb;
	
	@Column(name = "is_loaded")
	@ColumnDefault(value = "'true'")
	private Boolean isLoaded;
	
	// meta v2
	@Column(name = "layout")
	private String layout;
	
	@Column(name = "extra", columnDefinition = "TEXT")
	private String extra;
	
	@Column(name = "is_affix_tab")
	@ColumnDefault(value = "'false'")
	private Boolean isAffixTab;
	
	@Column(name = "affix_tab_order")
	private Integer affixTabOrder;
	
	@Column(name = "active_icon")
	private String activeIcon;
	
	@Column(name = "active_path")
	private String activePath;
	
	@Column(name = "badge")
	private String badge;
	
	@Column(name = "badge_type")
	private String badgeType;
	
	@Column(name = "badge_variants")
	private String badgeVariants;
	
	@Column(name = "is_ignore_access")
	private Boolean isIgnoreAccess;
	
	@Column(name = "is_hide_children_in_menu")
	private Boolean isHideChildrenInMenu;
	
	@Column(name = "is_hide_in_menu")
	private Boolean isHideInMenu;
	
	@Column(name = "is_hide_breadcrumb")
	private Boolean isHideInBreadcrumb;
	
	@Column(name = "is_hide_tab")
	private Boolean isHideInTab;
	
	@Column(name = "iframe_src", columnDefinition = "TEXT")
	private String iframeSrc;
	
	@Column(name = "is_keep_alive")
	private Boolean isKeepAlive;
	
	@Column(name = "max_num_of_open_tab")
	private Integer maxNumOfOpenTab;
	
	@Column(name = "is_menu_visible_with_forbidden")
	private Boolean isMenuVisibleWithForbidden;
	
	@Column(name = "is_no_basic_layout")
	private Boolean isNoBasicLayout;
	
	@Column(name = "is_open_in_newwindow")
	private Boolean isOpenInNewWindow;
	
	@Column(name = "query", columnDefinition = "TEXT")
	private String query;
	
	// Các trường bổ sung
	@Column(name = "is_reload")
	@ColumnDefault(value = "'true'")
	private Boolean isReload;
	
	@Column(name = "app_code", length = 50)
	private String appCode;
	
	@Column(name = "extra_meta", columnDefinition = "JSONB")
	private String extraMeta;
	
}
