package vn.tr.core;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"vn.tr.core", "vn.tr.common"})
public class CoreServiceApplication {
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(CoreServiceApplication.class).run(args);
	}
	
}
