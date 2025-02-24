package vn.tr.core.config;

import cn.dev33.satoken.stp.StpUtil;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
@Slf4j
public class FeignClientConfig {
	
	@Bean
	RequestInterceptor requestInterceptor() {
		return requestTemplate -> {
			String token = StpUtil.getTokenInfo().getTokenValue();
			if (Objects.isNull(token)) {
				StpUtil.login("888888");
				token = StpUtil.getTokenInfo().getTokenValue();
			}
			log.info("Token: {}", token);
			requestTemplate.header("X-Token", "Bearer " + token);
		};
	}
	
	//	@Bean
	//	public ErrorDecoder errorDecoder() {
	//		return new FeignErrorDecoder();
	//	}
	
}
