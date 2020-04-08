package spring.context.annotation.web.servlet.support;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import spring.context.annotation.web.config.DispatcherServletConfig;

public class DefaultAbstractAnnotationConfigDispatcherServletInitializer extends
		AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() { //对应web.xml
		return new Class[0];
	}

	@Override
	protected Class<?>[] getServletConfigClasses() { //DispatcherServlet
		return new Class[]{DispatcherServletConfig.class};
	}

	@Override
	protected String[] getServletMappings() { //路径映射
		return new String[]{"/"};
	}
}
