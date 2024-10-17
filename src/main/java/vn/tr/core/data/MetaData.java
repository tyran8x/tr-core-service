package vn.tr.core.data;

import lombok.Data;

@Data
public class MetaData {

	private String title;

	private String icon;

	private Boolean noCache;

	private Boolean affix;

	private Boolean breadcrumb;

	private String activeMenu;

	private String component;

	private String link;

	private Boolean isIframe;

	private String[] roles;

}
