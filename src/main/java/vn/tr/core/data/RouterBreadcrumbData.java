package vn.tr.core.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouterBreadcrumbData {
	
	private String title;
	private String path; // Đường dẫn (tùy chọn, nếu breadcrumb có thể click)
	private String name; // Tên route (tùy chọn, nếu breadcrumb có thể click và dùng name)
	private Boolean noLink; // boolean noLink; // Tùy chọn: đánh dấu breadcrumb này không phải là link
}
