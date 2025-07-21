package vn.tr.core;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients
@SpringBootApplication
@ComponentScan(basePackages = {"vn.tr.core", "vn.tr.common"})
public class CoreServiceApplication {
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(CoreServiceApplication.class).run(args);
	}
	
}
