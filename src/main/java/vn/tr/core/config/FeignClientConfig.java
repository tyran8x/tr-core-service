//package vn.tr.core.config;
//
//import cn.dev33.satoken.exception.SaTokenException;
//import cn.dev33.satoken.stp.StpUtil;
//import feign.RequestInterceptor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@Slf4j
//public class FeignClientConfig {
//
//	private static final long SYSTEM_LOGIN_ID = 999994;
//	private static final Object LOCK = new Object();
//	private static volatile String cachedToken = null;
//
//	@Bean
//	RequestInterceptor requestInterceptor() {
//		return requestTemplate -> {
//			String systemToken = getOrCreateSystemToken();
//			if (systemToken != null) {
//				log.debug("Applying system token for ID {}", SYSTEM_LOGIN_ID);
//				requestTemplate.header("X-Token", "Bearer " + systemToken);
//			} else {
//				log.error("Failed to obtain system token for ID {}", SYSTEM_LOGIN_ID);
//			}
//		};
//	}
//
//	private String getOrCreateSystemToken() {
//		// Kiểm tra token trong cache
//		if (cachedToken != null && isTokenValid(cachedToken)) {
//			return cachedToken;
//		}
//
//		// Đồng bộ để tránh tạo nhiều token cùng lúc
//		synchronized (LOCK) {
//			// Kiểm tra lại sau khi đồng bộ
//			if (cachedToken != null && isTokenValid(cachedToken)) {
//				return cachedToken;
//			}
//
//			try {
//				// Tạo session và lấy token
//				StpUtil.createLoginSession(SYSTEM_LOGIN_ID);
//				cachedToken = StpUtil.getTokenValueByLoginId(SYSTEM_LOGIN_ID);
//
//				if (cachedToken != null) {
//					log.info("Generated new system token for ID {}", SYSTEM_LOGIN_ID);
//				} else {
//					log.error("Failed to generate system token for ID {}", SYSTEM_LOGIN_ID);
//				}
//				return cachedToken;
//
//			} catch (SaTokenException e) {
//				log.error("Error generating system token for ID {}", SYSTEM_LOGIN_ID, e);
//				return null;
//			}
//		}
//	}
//
//	private boolean isTokenValid(String token) {
//		try {
//			// Giả sử StpUtil có phương thức kiểm tra token
//			return StpUtil.isLogin(SYSTEM_LOGIN_ID) && token.equals(StpUtil.getTokenValueByLoginId(SYSTEM_LOGIN_ID));
//		} catch (Exception e) {
//			log.warn("Error validating token for ID {}", SYSTEM_LOGIN_ID, e);
//			return false;
//		}
//	}
//
//	//	@Bean
//	//	public ErrorDecoder errorDecoder() {
//	//		return new FeignErrorDecoder();
//	//	}
//
//}
