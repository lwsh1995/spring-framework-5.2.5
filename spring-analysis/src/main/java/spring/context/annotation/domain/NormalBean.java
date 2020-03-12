package spring.context.annotation.domain;

public class NormalBean {
	private String name;

	public NormalBean(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "NormalBean{" +
				"name='" + name + '\'' +
				'}';
	}
}
