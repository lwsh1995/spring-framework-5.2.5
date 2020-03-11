package spring.base.annotation.domain;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.*;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringValueResolver;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.Iterator;

public class AwareBean implements BeanNameAware, BeanClassLoaderAware, BeanFactoryAware, EnvironmentAware , EmbeddedValueResolverAware ,
		ResourceLoaderAware , ApplicationEventPublisherAware ,MessageSourceAware,ApplicationContextAware, ServletContextAware {


	private String beanName;

	private String beanClassLoader;

	private String properties;

	@Value("${value.one}")
	private String valueOne;

	private String valueTwo;

	private StringValueResolver stringValueResolver;

	@Override
	public void setBeanName(String name) {
		this.beanName=name;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader=classLoader.getClass().getName();
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory) beanFactory;
		Iterator<String> beanNamesIterator =
				listableBeanFactory.getBeanNamesIterator();
		while (beanNamesIterator.hasNext()){
			System.out.print(beanNamesIterator.next()+"\t");
		}
		System.out.println();
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.properties=environment.getProperty("user.name");
	}

	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		this.stringValueResolver=resolver;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		System.out.println("resource loader "+resourceLoader.getClass());
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		applicationEventPublisher.publishEvent(new EventBean("aware event bean "));
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println(applicationContext.getApplicationName()+applicationContext.getId());
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		System.out.println(servletContext.getContextPath());

	}

	@Override
	public String toString() {
		return "AwareBean{" +
				"beanName='" + beanName + '\'' +
				", beanClassLoader='" + beanClassLoader + '\'' +
				", properties='" + properties + '\'' +
				", valueOne='" + valueOne + '\'' +
				", valueTwo='" + stringValueResolver.resolveStringValue("${value.two}") + '\'' +
				", stringValueResolver=" + stringValueResolver +
				'}';
	}

}
