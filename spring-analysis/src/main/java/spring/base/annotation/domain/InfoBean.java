package spring.base.annotation.domain;

import lombok.Data;
import org.springframework.beans.factory.*;

@Data
public class InfoBean implements InitializingBean, DisposableBean {
	private String name;
	private Integer version;
	private String beanName;
	private String properties;

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

}
