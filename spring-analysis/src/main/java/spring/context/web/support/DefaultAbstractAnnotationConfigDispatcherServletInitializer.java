package spring.context.web.support;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import spring.context.web.config.ContextListenerConfig;
import spring.context.web.config.RootConfig;
import spring.context.web.config.WebConfig;

/**
 * 使用java类配置servlet容器，不在使用web.xml
 * 扩展AbstractAnnotationConfigDispatcherServletInitializer的java类会自动配置
 * DispatcherServlet和spring应用上下文
 *
 * 在servlet3.0中，servlet容器会在类路径中查找实现ServletContainerInitializer接口的类，并用该类配置容器
 * spring提供了该接口的实现SpringServletContainerInitializer，该类又会查找实现WebApplicationInitializer的类并配置
 *
 * 当DispatcherServlet启动时，会创建Spring应用上下文，并加载配置文件或配置类声明的bean，包含web组件如控制器、视图解析器、
 * 处理器映射
 *
 * ContextLoaderListener则加载应用中的其他bean，通常是驱动应用后端的中间层和数据层组件
 *
 */
public class DefaultAbstractAnnotationConfigDispatcherServletInitializer extends
		AbstractAnnotationConfigDispatcherServletInitializer {

	//返回带有@Configuration注解的类将配置ContextLoaderListener创建的应用上下文的bean
	@Override
	protected Class<?>[] getRootConfigClasses() {
		System.out.println("root config");
		return new Class[]{RootConfig.class, ContextListenerConfig.class};
	}

	//返回带有@Configuration注解的类将定义DispatcherServlet上下文中的bean
	@Override
	protected Class<?>[] getServletConfigClasses() {
		System.out.println("servlet config");
		return new Class[]{WebConfig.class};
	}

	//一个或多个路径映射到DispatcherServlet上，/ 表示应用默认Servlet，会处理进入应用的所有请求
	@Override
	protected String[] getServletMappings() {
		System.out.println("servlet mapping");
		return new String[]{"/"};
	}
}
