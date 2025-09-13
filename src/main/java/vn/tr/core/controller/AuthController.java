package vn.tr.core.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.domain.R;
import vn.tr.common.core.domain.model.LoginBody;
import vn.tr.common.core.domain.model.LoginUser;
import vn.tr.common.core.domain.model.RegisterBody;
import vn.tr.common.core.utils.ValidatorUtils;
import vn.tr.common.encrypt.annotation.ApiEncrypt;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.common.web.annotation.AppCode;
import vn.tr.common.websocket.dto.WebSocketMessageDto;
import vn.tr.common.websocket.utils.WebSocketUtils;
import vn.tr.core.dao.service.CoreUserService;
import vn.tr.core.data.LoginResult;
import vn.tr.core.data.dto.CoreClientData;
import vn.tr.core.security.service.IAuthStrategy;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
	
	private final CoreUserService coreUserService;
	private final ScheduledExecutorService scheduledExecutorService;
	
	@PostMapping("/login")
	public R<LoginResult> login(@RequestBody LoginBody loginBody, @AppCode String appCode) {
		ValidatorUtils.validate(loginBody);
		
		log.info("APPCODE: {}", appCode);
		String clientId = loginBody.getClientId();
		CoreClientData coreClientData = new CoreClientData();
		//		// client grantType
		//		if (ObjectUtil.isNull(client) || !StringUtils.contains(client.getGrantType(), grantType)) {
		//			return R.fail(MessageUtils.message("auth.grant.type.error"));
		//		} else if (!UserConstants.NORMAL.equals(client.getTrangThai())) {
		//			return R.fail(MessageUtils.message("auth.grant.type.blocked"));
		//		}
		LoginResult loginResult = IAuthStrategy.executeLogin(loginBody, coreClientData, appCode);
		
		Long userId = LoginHelper.getUserId();
		scheduledExecutorService.schedule(() -> {
			WebSocketMessageDto webSocketMessageDto = new WebSocketMessageDto();
			webSocketMessageDto.setMessage("Tr-Pro-App");
			webSocketMessageDto.setSessionKeys(List.of(userId));
			WebSocketUtils.publishMessage(webSocketMessageDto);
		}, 3, TimeUnit.SECONDS);
		return R.ok(loginResult);
	}
	
	@ApiEncrypt
	@PostMapping("/register")
	public R<Void> register(@RequestBody RegisterBody registerBody) {
		CoreClientData coreClientData = new CoreClientData();
		IAuthStrategy.executeRegister(registerBody, coreClientData);
		return R.ok();
	}
	
	@ApiEncrypt
	@GetMapping("/info")
	public R<LoginUser> info() {
		LoginUser getLoginUser = LoginHelper.getLoginUser();
		return R.ok(getLoginUser);
	}
	
	@DeleteMapping("/logout")
	public R<Void> logout() {
		coreUserService.logout();
		return R.ok();
	}
	
}
