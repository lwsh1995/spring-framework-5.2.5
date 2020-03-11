package spring.context.annotation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import spring.context.annotation.domain.NormalBean;

@Configuration
public class BeanConfig {

	@Bean
	NormalBean normalBean(@Value("${normal.name}")String name){
		return new NormalBean(name);
	}
}
