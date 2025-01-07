package vn.tr.core;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class CoreServiceApplication {
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(CoreServiceApplication.class).run(args);
	}
	
}
