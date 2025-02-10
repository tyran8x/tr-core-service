package vn.tr.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
	
	@Bean
	public OpenAPI openApi() {
		return new OpenAPI()
				.components(
						new Components()
								.addSecuritySchemes("apiKeyScheme",
										new SecurityScheme()
												.type(SecurityScheme.Type.APIKEY)
												.in(SecurityScheme.In.HEADER)
												.name("X-TOKEN")
								                   )
				           )
				.addSecurityItem(
						new SecurityRequirement()
								.addList("apiKeyScheme")
				                )
				.info(
						new Info()
								.title("CORE API")
								.version("0.1")
								.license(
										new License()
												.name("Apache 2.0")
												.url("http://dev.dnict.vn")
								        )
				     );
	}
	
}
