package vn.tr.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

@SpringBootApplication
@Slf4j
public class CoreServiceApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(CoreServiceApplication.class);
		application.setApplicationStartup(new BufferingApplicationStartup(2048));
		application.run(args);
		System.out.println("(♥◠‿◠)ﾉﾞ  Core-Service   ლ(´ڡ`ლ)ﾞ");
	}

	//	@Bean
	//	public CasAuthenticationFilter casAuthenticationFilter(AuthenticationManager authenticationManager,
	//			ServiceProperties serviceProperties) throws Exception {
	//		CasAuthenticationFilter filter = new CasAuthenticationFilter();
	//		filter.setAuthenticationManager(authenticationManager);
	//		filter.setServiceProperties(serviceProperties);
	//		return filter;
	//	}
	//
	//	@Bean
	//	public ServiceProperties serviceProperties() {
	//		log.info("service properties");
	//		ServiceProperties serviceProperties = new ServiceProperties();
	//		serviceProperties.setService("http://localhost:8989/login");
	//		serviceProperties.setSendRenew(false);
	//		return serviceProperties;
	//	}
	//
	//	@Bean
	//	public TicketValidator ticketValidator() {
	//		return new Cas20ServiceTicketValidator("https://dangnhap.danang.gov.vn/cas");
	//	}
	//
	//	@Bean
	//	public CasUserDetailsService getUser() {
	//		return new CasUserDetailsService();
	//	}
	//
	//	@Bean
	//	public CasAuthenticationProvider casAuthenticationProvider(
	//			TicketValidator ticketValidator,
	//			ServiceProperties serviceProperties) {
	//		CasAuthenticationProvider provider = new CasAuthenticationProvider();
	//		provider.setServiceProperties(serviceProperties);
	//		provider.setTicketValidator(ticketValidator);
	//		provider.setUserDetailsService(getUser());
	//		//For Authentication with a Database-backed UserDetailsService
	//		//provider.setUserDetailsService(getUser());
	//		provider.setKey("CAS_PROVIDER_LOCALHOST_8989");
	//		return provider;
	//	}
	//
	//	@Bean
	//	public SecurityContextLogoutHandler securityContextLogoutHandler() {
	//		return new SecurityContextLogoutHandler();
	//	}
	//
	//	@Bean
	//	public LogoutFilter logoutFilter() {
	//		LogoutFilter logoutFilter = new LogoutFilter("https://dangnhap.danang.gov.vn/cas/logout", securityContextLogoutHandler());
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

}
