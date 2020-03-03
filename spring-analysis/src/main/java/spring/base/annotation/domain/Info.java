package spring.base.annotation.domain;


import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

@Data
public class Info implements InitializingBean, DisposableBean {
	private String name;
	private Integer version;

	private String properties;

	public Info(String name, Integer version) {
		this.name = name;
		this.version = version;
	}

	@Override
	public String toString() {
		return "Info{" +
				"name='" + name + '\'' +
				", version=" + version +
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
