package vn.tr.core.data.dto;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.HeadFontStyle;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Accessors(chain = true) // The import is not allowed to use. The set method cannot be found.
@HeadFontStyle(fontName = "Time new Roman")
public class CoreUserImportData implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;
	
	@ExcelProperty(value = "userId")
	private Long userId;
	
	@ExcelProperty(value = "Username")
	private String username;
	
	@ExcelProperty(value = "FullName")
	private String fullName;
	
	@ExcelProperty(value = "Email")
	private String email;
	
	@ExcelProperty(value = "Status")
	private String status;
	
}
