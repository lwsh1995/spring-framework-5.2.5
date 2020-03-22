package spring.context.annotation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import spring.context.annotation.domain.InfoFactoryBean;
import spring.context.annotation.domain.InitBean;
import spring.context.annotation.domain.NormalBean;

@Configuration
public class BeanConfig {

	class InnerConfig{
		@Bean("inner")
		NormalBean normalBean(){
			return new NormalBean("inner bean");
		}
	}

	@Bean
	NormalBean normalBean(@Value("${normal.name}")String name){
		return new NormalBean(name);
	}

	@Bean
	InfoFactoryBean infoFactoryBean(@Value("${info}")String info){
		InfoFactoryBean infoFactoryBean = new InfoFactoryBean(info);
		return infoFactoryBean;
	}

	@Bean
	InitBean initBean(){
		return new InitBean();
	}
}
