package vn.tr.core.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CoreRoleData {
	
	private Long id;
	
	@NotBlank(message = "Vui lòng nhập tên")
	@Size(max = 250, message = "Nhập tên quá {max} ký tự")
	private String ten;
	
	@NotBlank(message = "Vui lòng nhập mã")
	@Size(max = 50, message = "Nhập mã quá {max} ký tự")
	private String ma;
	
	private String moTa;
	
	@NotNull(message = "Vui lòng chọn trạng thái")
	private Boolean trangThai;
	
	private Boolean isDefault;
	
	private String appCode;
	
	private List<String> menus = new ArrayList<>();
}
