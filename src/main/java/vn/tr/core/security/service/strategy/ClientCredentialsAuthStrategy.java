package vn.tr.core.security.service.strategy;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.tr.common.core.constant.SecurityConstants;
import vn.tr.common.core.domain.model.LoginBody;
import vn.tr.common.core.domain.model.LoginUser;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.core.data.LoginResult;
import vn.tr.core.data.dto.CoreClientData;
import vn.tr.core.security.service.IAuthStrategy;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Service("client_credentials" + IAuthStrategy.BASE_NAME)
public class ClientCredentialsAuthStrategy implements IAuthStrategy {
	
	@Override
	public LoginResult performLogin(LoginBody loginBody, CoreClientData client, String appCode) {
		log.info("Performing login for client_credentials grant type, clientId: {}", client.getClientId());
		
		// 1. Tạo một LoginUser "ảo" đại diện cho chính client
		LoginUser clientAsUser = new LoginUser();
		// Login ID có thể là clientId để dễ nhận biết
		clientAsUser.setUserId(client.getClientId());
		clientAsUser.setUsername("client_" + client.getClientId());
		
		// 2. Gán các vai trò/quyền đặc biệt cho client này
		// Ví dụ, tất cả các client M2M đều có vai trò ROLE_SERVICE
		clientAsUser.setRoleCodes(Set.of("ROLE_SERVICE"));
		clientAsUser.setPermissionCodes(Collections.emptySet()); // Hoặc lấy từ một cấu hình riêng
		clientAsUser.setAppCode(client.getAppCode());
		
		// 3. Thực hiện đăng nhập Sa-Token
		SaLoginParameter saLoginParameter = new SaLoginParameter()
				.setTimeout(client.getTimeout())
				.setExtra(SecurityConstants.CLIENT_ID, client.getClientId()); // Nhấn mạnh đây là client
		
		StpUtil.login(clientAsUser.getUserId(), saLoginParameter);
		StpUtil.getSession().set(LoginHelper.LOGIN_USER_KEY, clientAsUser);
		
		// 4. Trả về token
		return LoginResult.builder()
				.accessToken(StpUtil.getTokenValue())
				.expireIn(StpUtil.getTokenTimeout())
				.clientId(client.getClientId())
				.tokenType(SecurityConstants.TOKEN_PREFIX.trim())
				.build();
	}
	
}
