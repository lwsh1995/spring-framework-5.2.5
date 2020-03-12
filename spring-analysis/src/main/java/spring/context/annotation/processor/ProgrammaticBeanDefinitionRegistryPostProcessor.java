package spring.context.annotation.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;
import spring.context.annotation.domain.NormalBean;

/**
 * 在spring中第一次对加载的BeanDefinition进行修改
 * 该PostProcessor被加入到spring容器中有两种方式
 * 一：通过注解的方式被spring容器扫描，被注册ConfigurationClassPostProcessor处理器解析
 * 二：在context上下文中通过register(xxx.class)方式，并手动refresh进行刷新
 */
@Component
public class ProgrammaticBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
	// 在spring容器中进行第一次的Bean修改以及注册，使用硬编码的方式加入一个新bean
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		RootBeanDefinition normalBean = new RootBeanDefinition(NormalBean.class);
		registry.registerBeanDefinition("normal",normalBean);
	}

	// 在spring容器中对BeanDefinition修改
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		RootBeanDefinition normal =(RootBeanDefinition) beanFactory.getBeanDefinition("normal");
		ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
		constructorArgumentValues.addGenericArgumentValue("BeanDefinitionRegistryPostProcessor postProcessBeanFactory");
		normal.setConstructorArgumentValues(constructorArgumentValues);
	}
}
