package spring.test;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import spring.base.annotation.domain.EventBean;

public class BeanDefinitionTest {
	public static void main(String[] args) {
		GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
		genericBeanDefinition.setBeanClass(EventBean.class);
	}
}
