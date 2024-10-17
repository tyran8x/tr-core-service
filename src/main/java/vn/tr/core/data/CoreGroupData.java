package vn.tr.core.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CoreGroupData {

	private Long id;

	private Long chaId;

	private CoreGroupData chaData = new CoreGroupData();

	@NotBlank(message = "Vui lòng nhập tên")
	@Size(max = 250, message = "Nhập tên quá {max} ký tự")
	private String ten;

	@NotBlank(message = "Vui lòng nhập mã")
	@Size(max = 50, message = "Nhập mã quá {max} ký tự")
	private String ma;

	private String moTa;

	@NotNull(message = "Vui lòng chọn trạng thái")
	private Boolean trangThai;

	private Integer sapXep;

	private String appCode;

}
