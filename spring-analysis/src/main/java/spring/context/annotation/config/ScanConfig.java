package spring.context.annotation.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = {
		"spring.context.annotation.processor",
		"spring.context.annotation.component",
		"spring.context.annotation.aop",
		"spring.context.annotation.dao"
})
public class ScanConfig {
}
