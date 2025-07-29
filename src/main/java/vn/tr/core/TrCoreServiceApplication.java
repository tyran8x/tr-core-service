package vn.tr.core;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"vn.tr.core", "vn.tr.common"})
public class TrCoreServiceApplication {
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(TrCoreServiceApplication.class).run(args);
	}
	
}
