package spring.context.annotation.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import spring.context.annotation.domain.NormalBean;

/**
 * 在Bean实例化的前后进行处理
 * 如果返回一个新对象，则在context上下文则使用了新对象
 * 如果返回null，则context中继续使用对象
 */

@Component
public class ProgrammaticBeanPostProcessor implements BeanPostProcessor {
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (beanName.equals("normal")){
			return new NormalBean("bean processor before init");
		}
		return null;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (beanName.equals("normal")){
			return new NormalBean("bean processor after init");
		}
		return null;
	}
}
