package spring.context.web.config;

import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

@Configuration
public class ContextListenerConfig implements ServletContextListener {

	private ServletContext context=null;
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("init servlet context listener");
		this.context=sce.getServletContext();
		this.context.setAttribute("spring-analysis","analysis");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		this.context=null;
	}
}
