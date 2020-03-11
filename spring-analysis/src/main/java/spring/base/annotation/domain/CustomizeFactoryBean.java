package spring.base.annotation.domain;


public class CustomizeFactoryBean {
	private String name;

	public CustomizeFactoryBean(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "CustomizeFactoryBean{" +
				"name='" + name + '\'' +
				'}';
	}
}
