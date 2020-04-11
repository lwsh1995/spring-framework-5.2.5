package spring.context.annotation.web.servlet.support;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import spring.context.annotation.web.config.DispatcherServletConfig;

public class DefaultAbstractAnnotationConfigDispatcherServletInitializer extends
		AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() { //对应web.xml
		System.out.println("root config");
		return new Class[0];
	}

	@Override
	protected Class<?>[] getServletConfigClasses() { //DispatcherServlet
		System.out.println("servlet config");
		return new Class[]{DispatcherServletConfig.class};
	}

	@Override
	protected String[] getServletMappings() { //路径映射
		System.out.println("servlet mapping");
		return new String[]{"/"};
	}
}
