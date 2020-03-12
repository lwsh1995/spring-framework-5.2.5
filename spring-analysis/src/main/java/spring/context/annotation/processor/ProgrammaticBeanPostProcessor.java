package spring.context.annotation.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import spring.context.annotation.domain.NormalBean;

/**
 * 在Bean实例化后，在Bean的初始化的前后进行处理，在initializeBean(beanName, exposedObject, mbd)方法中被调用，
 * 该方法在doGetBean()获取一个Bean调用
 *
 * 如果BeanPostProcessor接口的方法返回一个新对象，则在context上下文使用新对象代替旧对象
 * 如果返回null，则context中继续使用对象
 *
 * initializeBean(beanName, exposedObject, mbd)方法流程
 *	->invokeAwareMethods(beanName, bean)	调用Aware接口
 *	->wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName) 接口返回新对象，则代替旧对象
 *	->invokeInitMethods(beanName, wrappedBean, mbd)	调用initMethod方法
 *	->wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName)	接口返回新对象，则代替旧对象
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
