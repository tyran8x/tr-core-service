package vn.tr.core.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteRecordRawData {
	
	private String path;
	
	private String name;
	
	private String component;
	
	/**
	 * Dành cho named views. Key là tên của router-view, value là tên/path component. Ví dụ: { "default": "MainComponent", "sidebar":
	 * "SidebarComponent" }
	 */
	private Map<String, String> components;
	
	private String redirect;
	
	/**
	 * Bí danh cho route. Ví dụ: ["/u", "/nguoi-dung"]
	 */
	private List<String> alias;
	
	private RouterMetaData meta;
	
	private List<RouteRecordRawData> children;
	
	private Object props;
	
}
