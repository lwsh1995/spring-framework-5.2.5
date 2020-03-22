package spring.context.annotation.domain;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class InitBean implements ApplicationContextAware,BeanPostProcessor, InitializingBean {

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println("aware :"+applicationContext.getId()+" "+applicationContext.getApplicationName());
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("bean post before initialization");
		return bean;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("initializing bean after properties");
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("bean post after initialization");
		return bean;
	}

}
