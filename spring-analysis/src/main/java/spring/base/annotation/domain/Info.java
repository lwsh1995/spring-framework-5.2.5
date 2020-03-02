package spring.base.annotation.domain;

public class Info {
	private String name;
	private Integer version;

	public Info() {
		System.out.println("info parent");
	}

	public Info(String name, Integer version) {
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
		return "Info{" +
				"name='" + name + '\'' +
				", version=" + version +
				'}';
	}
}
