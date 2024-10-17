package vn.tr.core.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class CoreUserData {

	private Long id;

	@Size(max = 250, message = "Nhập username quá {max} ký tự")
	private String userName;

	@NotBlank(message = "Vui lòng nhập email")
	@Size(max = 250, message = "Nhập email quá {max} ký tự")
	private String email;

	@Size(max = 50, message = "Nhập số điện thoại quá {max} ký tự")
	private String phoneNumber;

	@JsonIgnore
	private String password;

	@Size(max = 250, message = "Nhập nickName quá {max} ký tự")
	private String nickName;

	private String userType;

	private Set<String> roles = new HashSet<>();

	private String appCode;

	private Boolean isEnabled;

}
