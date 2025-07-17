package vn.tr.core.data.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CoreUserChangePasswordData {
	
	@NotEmpty
	private String password;
	
}
