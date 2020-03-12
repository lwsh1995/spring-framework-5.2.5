package spring.context.annotation.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
		"spring.context.annotation.processor",
		"spring.context.annotation.component"
})
public class ScanConfig {
}
