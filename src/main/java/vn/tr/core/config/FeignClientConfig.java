package vn.tr.core.config;

import cn.dev33.satoken.stp.StpUtil;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Objects;

@Configuration
@Slf4j
public class FeignClientConfig {
	
	private static String getToken() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8900/uaa/token");
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor("admin@gmail.com", "password"));
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, String.class);
		return responseEntity.getBody();
	}
	
	@Bean
	RequestInterceptor requestInterceptor() {
		return requestTemplate -> {
			String token = StpUtil.getTokenInfo().getTokenValue();
			if (Objects.isNull(token)) {
				token = getToken();
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
