package spring.base.annotation.domain;

public class User {
	private String name;
	private Integer version;

	public User(String name, Integer version) {
		this.name = name;
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public Integer getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return "User{" +
				"name='" + name + '\'' +
				", version=" + version +
				'}';
	}
}
