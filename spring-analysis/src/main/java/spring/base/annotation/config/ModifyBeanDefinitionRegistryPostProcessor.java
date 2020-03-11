package spring.base.annotation.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionVisitor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

@Component
public class ModifyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

	private String event="nCoV";

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

/*		String[] beanNames = registry.getBeanDefinitionNames();
		for (String beanName:beanNames) {
			GenericBeanDefinition beanDefinition =(GenericBeanDefinition) registry.getBeanDefinition(beanName);
			StringValueResolver svr=strVal -> {
				if (strVal.equals(event)){
					return "modify ......";
				}
				return strVal;
			};
			BeanDefinitionVisitor beanDefinitionVisitor = new BeanDefinitionVisitor(svr);
			beanDefinitionVisitor.visitBeanDefinition(beanDefinition);

		}*/
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		BeanDefinition eventBean = beanFactory.getBeanDefinition("eventBean");
		eventBean.setAttribute("name","name modify postProcessBeanFactory");
	}
}
