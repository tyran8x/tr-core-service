//package vn.tr.core.config;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apereo.cas.client.session.SingleSignOutFilter;
//import org.apereo.cas.client.validation.Cas30ServiceTicketValidator;
//import org.apereo.cas.client.validation.TicketValidator;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.ProviderManager;
//import org.springframework.security.cas.ServiceProperties;
//import org.springframework.security.cas.authentication.CasAuthenticationProvider;
//import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
//import org.springframework.security.cas.web.CasAuthenticationFilter;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.security.web.authentication.logout.LogoutFilter;
//import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
//import vn.tr.core.security.service.CustomUserDetailsService;
//
//import java.util.Collections;
//
//@Configuration
//@RequiredArgsConstructor
//@Slf4j
//public class CasConfig {
//
//	private final CustomUserDetailsService customUserDetailsService;
//	@Value("${cas.service.login}")
//	private String CAS_URL_LOGIN;
//	@Value("${cas.service.logout}")
//	private String CAS_URL_LOGOUT;
//	@Value("${cas.url.prefix}")
//	private String CAS_URL_PREFIX;
//	@Value("${app.service.security}")
//	private String CAS_SERVICE_URL;
//	@Value("${app.service.home}")
//	private String APP_SERVICE_HOME;
//
//	@Bean
//	public ServiceProperties serviceProperties() {
//		log.info("service properties");
//		ServiceProperties serviceProperties = new ServiceProperties();
//		serviceProperties.setService(CAS_SERVICE_URL);
//		serviceProperties.setSendRenew(false);
//		return serviceProperties;
//	}
//
//	@Bean
//	public CasAuthenticationFilter casAuthenticationFilter(AuthenticationManager authenticationManager, ServiceProperties serviceProperties) throws
//	Exception {
//		CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
//		casAuthenticationFilter.setAuthenticationManager(authenticationManager);
//		casAuthenticationFilter.setServiceProperties(serviceProperties);
//		return casAuthenticationFilter;
//	}
//
//	@Bean
//	public TicketValidator ticketValidator() {
//		return new Cas30ServiceTicketValidator(CAS_URL_PREFIX);
//	}
//
//	@Bean
//	public CasAuthenticationProvider casAuthenticationProvider() {
//		log.info("casAuthenticationProvider");
//		CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
//		casAuthenticationProvider.setServiceProperties(serviceProperties());
//		casAuthenticationProvider.setTicketValidator(ticketValidator());
//		casAuthenticationProvider.setAuthenticationUserDetailsService(customUserDetailsService);
//		casAuthenticationProvider.setKey("CAS_PROVIDER_LOCALHOST_9000");
//		return casAuthenticationProvider;
//	}
//
//	@Bean
//	public SecurityContextLogoutHandler securityContextLogoutHandler() {
//		return new SecurityContextLogoutHandler();
//	}
//
//	@Bean
//	public LogoutFilter logoutFilter() {
//		LogoutFilter logoutFilter = new LogoutFilter(CAS_URL_LOGOUT + "?service=" + CAS_SERVICE_URL, securityContextLogoutHandler());
//		logoutFilter.setFilterProcessesUrl("/logout");
//		return logoutFilter;
//	}
//
//	@Bean
//	public SingleSignOutFilter singleSignOutFilter() {
//		SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
//		SingleSignOutFilter.setLogoutCallbackPath("/exit/cas");
//		singleSignOutFilter.setIgnoreInitConfiguration(true);
//		return singleSignOutFilter;
//	}
//
//	@Bean
//	public AuthenticationEntryPoint authenticationEntryPoint() {
//		CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
//		casAuthenticationEntryPoint.setLoginUrl(CAS_URL_LOGIN);
//		casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
//		return casAuthenticationEntryPoint;
//	}
//
//	@Bean
//	protected AuthenticationManager authenticationManager() {
//		return new ProviderManager(Collections.singletonList(casAuthenticationProvider()));
//	}
//
//}
