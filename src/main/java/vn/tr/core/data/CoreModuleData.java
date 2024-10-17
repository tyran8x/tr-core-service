package vn.tr.core.data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import vn.tr.common.feign.bean.FileDinhKem;

import java.util.ArrayList;
import java.util.List;

@Data
public class CoreModuleData {

	private Long id;

	@NotBlank(message = "Vui lòng nhập tên")
	@Size(max = 250, message = "Nhập tên quá {max} ký tự")
	private String ten;

	@NotBlank(message = "Vui lòng chọn/nhập mã")
	@Size(max = 150, message = "Nhập mã quá {max} ký tự")
	private String ma;

	private Long chaId;

	private String chaTen;

	private Boolean trangThai;

	@Min(value = 0, message = "Nhập sắp xếp không nhỏ hơn {value}")
	@Max(value = 32767, message = "Nhập sắp xếp không lớn hơn {value}")
	private Integer sapXep;

	private List<Long> fileDinhKemIds = new ArrayList<>();

	private FileDinhKem fileDinhKem = new FileDinhKem();

	private String hinhAnh;
}
