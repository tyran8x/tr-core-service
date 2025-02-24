package vn.tr.core.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CoreUserChangeIsEnabledData {
	
	private String userName;
	
	@NotNull(message = "Vui lòng chọn trạng thái")
	private Boolean isEnabled;
	
}
