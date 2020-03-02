package spring.base.annotation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import spring.base.annotation.domain.User;

@Configuration
public class AnnotationConfig {

	@Bean
	User user(){
		return new User("spring",1);
	}



}
