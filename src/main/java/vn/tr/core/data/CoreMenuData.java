package vn.tr.core.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CoreMenuData {

	private Long id;

	@NotBlank(message = "Vui lòng nhập tên")
	@Size(max = 1000, message = "Nhập tên quá {max} ký tự")
	private String ten;

	@NotBlank(message = "Vui lòng nhập tên")
	@Size(max = 150, message = "Nhập tên quá {max} ký tự")
	private String ma;

	private Long chaId;

	private String chaTen;

	private String moTa;

	private String path;

	private String component;

	private String redirect;

	private Boolean isHidden;

	private String icon;

	private Boolean isAlwaysShow;

	private Boolean isNoCache;

	private Boolean isAffix;

	private Boolean isBreadcrumb;

	private String link;

	private Boolean isIframe;

	private String activeMenu;

	private String props;

	private Integer sapXep;

	private Boolean isReload;

	private Boolean trangThai;

	private String appCode;

	private List<CoreMenuData> children = new ArrayList<>();

}
