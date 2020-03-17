package spring.context.annotation.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {
		"spring.context.annotation.processor",
		"spring.context.annotation.component",
		"spring.context.annotation.aop"
})
public class ScanConfig {
}
