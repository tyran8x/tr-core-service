//package vn.tr.core.config;
//
//import io.github.wimdeblauwe.errorhandlingspringbootstarter.UnauthorizedEntryPoint;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.access.AccessDeniedHandler;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import vn.tr.common.web.filter.JwtAuthenticationFilter;
//
//@Slf4j
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//	private static final String[] WHITE_LIST_URL = {
//			"/auth/login",
//			"/auth/register",
//			"/auth/captcha/code"
//	};
//
//	@Bean
//	@Order(Ordered.HIGHEST_PRECEDENCE)
//	SecurityFilterChain apiSecurityFilterChain(HttpSecurity httpSecurity, UnauthorizedEntryPoint unauthorizedEntryPoint,
//			AccessDeniedHandler accessDeniedHandler) throws Exception {
//		httpSecurity
//				.securityMatcher("/auth/**")
//				.csrf(AbstractHttpConfigurer::disable)
//				.httpBasic(HttpBasicConfigurer::disable)
//				.authorizeHttpRequests(requestMatcherRegistry ->
//						requestMatcherRegistry.requestMatchers(WHITE_LIST_URL)
//								.permitAll()
//								.anyRequest().authenticated()
//				)
//				.exceptionHandling(exceptionHandling -> exceptionHandling
//						.authenticationEntryPoint(unauthorizedEntryPoint)
//						.accessDeniedHandler(accessDeniedHandler)
//				)
//				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//		return httpSecurity.build();
//	}
//
//	@Bean
//	@Order(2)
//	public SecurityFilterChain baseSecurityFilterChain(HttpSecurity httpSecurity, UnauthorizedEntryPoint unauthorizedEntryPoint,
//			AccessDeniedHandler accessDeniedHandler) throws Exception {
//
//		httpSecurity
//				.csrf(AbstractHttpConfigurer::disable)
//				.httpBasic(HttpBasicConfigurer::disable)
//				.authorizeHttpRequests(customizer -> customizer.anyRequest().authenticated())
//				.exceptionHandling(exceptionHandling -> exceptionHandling
//						.accessDeniedHandler(accessDeniedHandler)
//						.authenticationEntryPoint(unauthorizedEntryPoint)
//				)
//				.addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//
//		return httpSecurity.build();
//	}
//
//	//	@Bean
//	//	@Order(2)
//	//	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//	//		http
//	//				.authorizeHttpRequests(authorize -> authorize
//	//						.requestMatchers("/secured", "/login").authenticated()
//	//				)
//	//				.exceptionHandling(e -> e.authenticationEntryPoint(authenticationEntryPoint()))
//	//				//		.logout(logout -> logout.logoutSuccessUrl("/logout"))
//	//				.addFilterBefore(singleSignOutFilter, CasAuthenticationFilter.class)
//	//				.addFilterBefore(logoutFilter, LogoutFilter.class);
//	//		http.csrf(csrf -> csrf.ignoringRequestMatchers("/exit/cas"));
//	//		return http.build();
//	//	}
//	//
//	//	@Bean
//	//	public AuthenticationManager authManager(HttpSecurity http) throws Exception {
//	//		return http.getSharedObject(AuthenticationManagerBuilder.class)
//	//				.authenticationProvider(casAuthenticationProvider)
//	//				.build();
//	//	}
//	//
//	//	public AuthenticationEntryPoint authenticationEntryPoint() {
//	//		CasAuthenticationEntryPoint entryPoint = new CasAuthenticationEntryPoint();
//	//		entryPoint.setLoginUrl("https://dangnhap.danang.gov.vn/cas/login");
//	//		entryPoint.setServiceProperties(serviceProperties);
//	//		return entryPoint;
//	//	}
//
//}
