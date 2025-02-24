package vn.tr.core.data;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CoreUserChangePasswordData {
	
	private String userName;
	
	@NotBlank(message = "Vui lòng nhập password")
	private String password;
	
}
