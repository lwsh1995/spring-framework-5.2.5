package spring.context.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc //启用spring mvc
@ComponentScan("spring.context.web") //启用组件扫描
public class WebConfig  extends WebMvcConfigurationSupport {

	/**
	 * 配置jsp视图解析器
	 * 没有配置视图解析器，spring默认使用BeanNameViewResolver,解析器会查找ID与试图名称匹配的bean，
	 * 查找的bean要实现View接口
	 */
	@Bean
	public ViewResolver viewResolver(){
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
//		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/WEB-INF/jsp/");
		viewResolver.setSuffix(".jsp");
		viewResolver.setExposeContextBeansAsAttributes(true);
		return viewResolver;
	}

	/**
	 * 配置静态资源处理
	 */
	@Override
	protected void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		// enable，要求DispatcherServlet将对静态资源的请求转发到servlet容器默认的servlet上，不是用DispatcherServlet处理
		configurer.enable();
	}

}
