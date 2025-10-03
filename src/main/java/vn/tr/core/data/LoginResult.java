package vn.tr.core.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResult {
	
	private String clientId;
	
	private String accessToken;
	
	private String tokenType;
	
	// private String refreshToken;
	
	private Long expireIn;
	
}
