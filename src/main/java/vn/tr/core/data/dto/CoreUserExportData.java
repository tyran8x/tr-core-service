package vn.tr.core.data.dto;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class CoreUserExportData implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;
	
	@ExcelProperty(value = "UserId")
	private Long userId;
	
	@ExcelProperty(value = "Username")
	private String username;
	
	@ExcelProperty(value = "FullName")
	private String fullName;
	
	@ExcelProperty(value = "Email")
	private String email;

//	@ExcelProperty(value = "Account status", converter = ExcelDictConvert.class)
//	@ExcelDictFormat(dictType = "LifecycleStatus")
//	private String status;

}
