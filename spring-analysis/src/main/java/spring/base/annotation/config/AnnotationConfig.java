package spring.base.annotation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import spring.base.annotation.domain.AwareBean;
import spring.base.annotation.domain.InfoBean;
import spring.base.annotation.domain.StaticValue;

@Configuration
@PropertySource({"classpath:application.properties"})
public class AnnotationConfig {

	@Bean("infoBean")
	InfoBean infoBean(){
		return new InfoBean("spring",1);
	}

	@Bean("awareBean")
	AwareBean awareBean(){
		return new AwareBean();
	}

	@Bean("staticValue")
	StaticValue staticValue(){
		return new StaticValue();
	}

}
