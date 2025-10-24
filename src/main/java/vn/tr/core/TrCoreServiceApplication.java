package vn.tr.core;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"vn.tr"})
@OpenAPIDefinition(
		servers = {
				@Server(url = "/v2/core", description = "Core System v2")
		},
		info = @Info(title = "Core API ", version = "2.0", description = "Documentation Core API v2.0")
)
public class TrCoreServiceApplication {
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(TrCoreServiceApplication.class).run(args);
	}
	
}
