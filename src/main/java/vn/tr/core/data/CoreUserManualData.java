package vn.tr.core.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import vn.tr.common.feign.core.bean.FileDinhKem;

import java.util.ArrayList;
import java.util.List;

@Data
public class CoreUserManualData {

	private Long id;

	@NotBlank(message = "Vui lòng nhập tên")
	@Size(max = 3000, message = "Nhập tên quá {max} ký tự")
	private String ten;

	@Size(max = 500, message = "Nhập mã ứng dụng quá {max} ký tự")
	private String maUngDung;

	private Integer sapXep;

	private List<Long> fileDinhKemIds = new ArrayList<>();

	private FileDinhKem fileDinhKem = new FileDinhKem();

	private String appCode;

	private Boolean trangThai;

}
