package vn.tr.core.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouteRecordRawData {
	
	private String path;
	
	private String name;
	
	private Object component;
	
	private Integer sortOrder;
	
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
	
	private List<RouteRecordRawData> children = new ArrayList<>();
	
	private Object props;
	
}
