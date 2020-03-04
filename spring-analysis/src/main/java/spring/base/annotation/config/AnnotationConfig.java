package spring.base.annotation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import spring.base.annotation.domain.InfoBean;

@Configuration
@PropertySource({"classpath:application.properties"})
public class AnnotationConfig {

	@Bean("infoBean")
	InfoBean infoBean(){
		return new InfoBean("spring",1);
	}

}
