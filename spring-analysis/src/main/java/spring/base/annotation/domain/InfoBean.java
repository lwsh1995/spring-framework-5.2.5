package spring.base.annotation.domain;

import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.*;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

@Data
public class InfoBean implements InitializingBean, DisposableBean,FactoryBean<CustomizeFactoryBean>,
		BeanPostProcessor , InstantiationAwareBeanPostProcessor , BeanFactoryPostProcessor , ApplicationListener<EventBean> {
	private String name;
	private Integer version;
	private String beanName;
	private String properties;
	private String before;
	private String after;

	public InfoBean(String name, Integer version) {
		this.name = name;
		this.version = version;
	}

	@Override
	public String toString() {
		return "InfoBean{" +
				"name='" + name + '\'' +
				", version=" + version +
				", beanName='" + beanName + '\'' +
				", properties='" + properties + '\'' +
				", before='" + before + '\'' +
				", after='" + after + '\'' +
				'}';
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.properties="initializing bean";
	}

	@Override
	public void destroy() throws Exception {
		System.out.println("destroy bean");
	}

	@Override
	public CustomizeFactoryBean getObject() throws Exception {
		return new CustomizeFactoryBean("customize factory bean");
	}

	@Override
	public Class<?> getObjectType() {
		return CustomizeFactoryBean.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		this.before="before";
		System.out.println("before initial "+bean.getClass()+" "+beanName);
		return null;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		this.after="after";
		System.out.println("after initial "+bean.getClass()+" "+beanName);
		return null;
	}

	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		System.out.println("before instantiation "+beanClass+" "+beanName);
		return null;
	}

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		System.out.println("after instantiation "+bean.getClass()+" "+beanName);
		return false;
	}

	@Override
	public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
		System.out.println("process properties "+bean.getClass()+" "+beanName);
		return null;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		InfoBean ben = beanFactory.getBean("&infoBean", InfoBean.class);
		ben.setName("-----");
	}

	@Override
	public void onApplicationEvent(EventBean event) {
		System.out.println("event : "+event.toString());
	}

	public void setName(String name) {
		this.name = name;
	}
}
