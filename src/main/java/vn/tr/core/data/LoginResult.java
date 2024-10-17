package vn.tr.core.data;

import lombok.Data;

@Data
public class LoginResult {

	private String clientId;

	private String accessToken;

	private String tokenType;

	// private String refreshToken;

	private Long expireIn;

}
