package spring.base.annotation.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import spring.base.annotation.domain.Info;

@Configuration
public class AnnotationConfig {

	@Bean("info1")
	Info info(){
		return new Info("spring 1",1);
	}



}
