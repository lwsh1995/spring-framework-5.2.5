package spring.context.annotation.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

/**
 * 在spring中第二次对加载的BeanDefinition进行修改
 */
@Component
public class ProgrammaticBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	private String keywords="fuck";
	// 在spring容器中对BeanDefinition修改
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		String[] beans = beanFactory.getBeanDefinitionNames();
		for (String bean:beans){
			BeanDefinition bd = beanFactory.getBeanDefinition(bean);
			StringValueResolver stringValueResolver = new StringValueResolver() {
				public String resolveStringValue(String str) {
					if (keywords.equals(str)) {
						return "****";
					}
					return str;
				}
			};
			BeanDefinitionVisitor beanDefinitionVisitor = new BeanDefinitionVisitor(stringValueResolver);
			beanDefinitionVisitor.visitBeanDefinition(bd);
		}
	}
}
