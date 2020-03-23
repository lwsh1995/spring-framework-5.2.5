package spring.context.annotation.config;

import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import spring.context.annotation.domain.InfoFactoryBean;
import spring.context.annotation.domain.InitBean;
import spring.context.annotation.domain.NormalBean;
import spring.context.annotation.editor.DatePropertyEditor;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.HashMap;
import java.util.Map;

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

	@Bean
	CustomEditorConfigurer customEditorConfigurer(){
		CustomEditorConfigurer customEditorConfigurer = new CustomEditorConfigurer();
		Map<Class<?>, Class<? extends PropertyEditor>> customEditors=new HashMap<>();
		customEditors.put(java.util.Date.class,DatePropertyEditor.class);
		customEditorConfigurer.setCustomEditors(customEditors);
		return customEditorConfigurer;
	}
}
