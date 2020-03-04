package spring.base.annotation.domain;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.StringValueResolver;

import java.util.Iterator;

public class AwareBean implements BeanNameAware, BeanClassLoaderAware, BeanFactoryAware, EnvironmentAware , EmbeddedValueResolverAware {


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
