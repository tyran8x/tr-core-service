package vn.tr.core.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vn.tr.common.json.handler.XssPlainTextJsonDeserializer;
import vn.tr.common.web.data.dto.BaseData;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CoreMenuData extends BaseData {
	
	private Long parentId;
	
	@JsonIgnore
	private String appCode;
	
	@Builder.Default
	private BaseData parent = new BaseData();
	
	@JsonDeserialize(using = XssPlainTextJsonDeserializer.class)
	private String description;
	
	private Integer sortOrder;
	
	@JsonDeserialize(using = XssPlainTextJsonDeserializer.class)
	private String path;
	
	@JsonDeserialize(using = XssPlainTextJsonDeserializer.class)
	private String component;
	
	@JsonDeserialize(using = XssPlainTextJsonDeserializer.class)
	private String components;
	
	@JsonDeserialize(using = XssPlainTextJsonDeserializer.class)
	private String alias;
	
	@JsonDeserialize(using = XssPlainTextJsonDeserializer.class)
	private String props;
	
	// Các trường meta
	@JsonDeserialize(using = XssPlainTextJsonDeserializer.class)
	private String redirect;
	
	@JsonDeserialize(using = XssPlainTextJsonDeserializer.class)
	private String icon;
	
	@JsonDeserialize(using = XssPlainTextJsonDeserializer.class)
	private String activeMenu;
	
	@JsonDeserialize(using = XssPlainTextJsonDeserializer.class)
	private String link;
	
	private Boolean isHidden;
	
	private Boolean isAlwaysShow;
	
	private Boolean isNoCache;
	
	private Boolean isAffix;
	
	private Boolean isBreadcrumb;
	
	private Boolean isLoaded;
	
	private String layout;
	
	@JsonDeserialize(using = XssPlainTextJsonDeserializer.class)
	private String extra;
	
	private Boolean isAffixTab;
	
	private Integer affixTabOrder;
	
	@JsonDeserialize(using = XssPlainTextJsonDeserializer.class)
	private String activeIcon;
	
	@JsonDeserialize(using = XssPlainTextJsonDeserializer.class)
	private String activePath;
	
	@JsonDeserialize(using = XssPlainTextJsonDeserializer.class)
	private String badge;
	
	@JsonDeserialize(using = XssPlainTextJsonDeserializer.class)
	private String badgeType;
	
	@JsonDeserialize(using = XssPlainTextJsonDeserializer.class)
	private String badgeVariants;
	
	private Boolean isIgnoreAccess;
	
	private Boolean isHideChildrenInMenu;
	
	private Boolean isHideInMenu;
	
	private Boolean isHideInBreadcrumb;
	
	private Boolean isHideInTab;
	
	@JsonDeserialize(using = XssPlainTextJsonDeserializer.class)
	private String iframeSrc;
	
	private Boolean isKeepAlive;
	
	private Integer maxNumOfOpenTab;
	
	private Boolean isMenuVisibleWithForbidden;
	
	private Boolean isNoBasicLayout;
	
	private Boolean isOpenInNewWindow;
	
	@JsonDeserialize(using = XssPlainTextJsonDeserializer.class)
	private String query;
	
	private Boolean isReload;
	
	@JsonDeserialize(using = XssPlainTextJsonDeserializer.class)
	private String extraMeta;
	
}
