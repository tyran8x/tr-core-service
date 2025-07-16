package vn.tr.core.data;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CoreConfigDefinitionData {
	
	private Long id;
	
	@Size(max = 200, message = "Nhập mã ứng dụng quá {max} ký tự")
	private String maUngDung;
	
	@Size(max = 500, message = "Nhập code quá {max} ký tự")
	private String code;
	
	private Integer loaiGiaTri;
	
	@Size(max = 500, message = "Nhập giá trị quá {max} ký tự")
	private String giaTri;
	
	private String ghiChu;
	
	private Boolean trangThai;
	
}
