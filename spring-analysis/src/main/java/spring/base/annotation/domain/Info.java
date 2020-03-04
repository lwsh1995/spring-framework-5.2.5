package spring.base.annotation.domain;

import lombok.Data;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanNameAware;

@Data
public class Info implements BeanNameAware, BeanClassLoaderAware {
	private String name;
	private Integer version;
	private String beanName;

	public Info(String name, Integer version) {
		this.name = name;
		this.version = version;
	}

	@Override
	public void setBeanName(String name) {
		beanName=name;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		System.out.println("class loader: "+classLoader);
	}
}
