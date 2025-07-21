package vn.tr.core.security.service.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.tr.core.dao.service.CoreUserAppService;
import vn.tr.core.dao.service.CoreUserService;
import vn.tr.core.security.service.IAuthStrategy;

@Slf4j
@Service("password" + IAuthStrategy.BASE_NAME)
public class PasswordAuthStrategy extends BasePasswordStrategy {
	
	public PasswordAuthStrategy(CoreUserService coreUserService, CoreUserAppService coreUserAppService) {
		super(coreUserService, coreUserAppService);
	}
	
}
