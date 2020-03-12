package spring.context.annotation.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.stereotype.Component;

/**
 * 在spring中第二次对加载的BeanDefinition进行修改
 */
@Component
public class ProgrammaticBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	// 在spring容器中对BeanDefinition修改
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		RootBeanDefinition normal =(RootBeanDefinition) beanFactory.getBeanDefinition("normal");
		ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
		constructorArgumentValues.addGenericArgumentValue("BeanFactoryPostProcessor postProcessBeanFactory");
		normal.setConstructorArgumentValues(constructorArgumentValues);
	}
}
