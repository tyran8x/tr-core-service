/// **
// *
// */
//package vn.tr.core.security.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.codec.binary.Base64;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
//import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
//@Slf4j
//@Service
//public class CustomUserDetailsService implements AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {
//	//	private final TokenService tokenService;
//	//	@Value("${security.token.login}")
//	//	private String UAA_URL_LOGIN;
//
//	//public CustomUserDetailsService(TokenService tokenService) {
//	//	this.tokenService = tokenService;
//	//	}
//
//	@Override
//	public UserDetails loadUserDetails(CasAssertionAuthenticationToken token) throws UsernameNotFoundException {
//		String login = token.getPrincipal().toString();
//		String lowercaseLogin = login.toLowerCase().trim();
//		log.info("login '{}'", lowercaseLogin);
//		log.info("getCredentials '{}'", token.getCredentials().toString());
//		HttpHeaders headers = new HttpHeaders();
//
//		String auth = lowercaseLogin + ":" + "system";
//		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
//		String authHeader = "Basic " + new String(encodedAuth);
//		log.info("authHeader '{}'", authHeader);
//		headers.set("Authorization", authHeader);
//		headers.setAccept(List.of(MediaType.APPLICATION_JSON));
//		// Yêu cầu trả về định dạng JSON
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		HttpEntity<Void> entity = new HttpEntity<>(headers);
//		RestTemplate restTemplate = new RestTemplate();
//		//		ResponseEntity<String> response = restTemplate.exchange(UAA_URL_LOGIN, HttpMethod.GET, entity, String.class);
//		//		String tokenJWT = response.getBody();
//		//		System.out.println(response.getStatusCode());
//		//		System.out.println(tokenJWT);
//		//		if (response.getStatusCode() != HttpStatus.OK) {
//		//			//	throw new NotFoundException("Không tìm thấy user");
//		//		}
//		return null;// TokenUserDetails.fromUserObject(tokenService.decode(tokenJWT));
//	}
//}
