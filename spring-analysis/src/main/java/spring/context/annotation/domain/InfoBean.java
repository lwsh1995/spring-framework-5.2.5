package spring.context.annotation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

public class InfoBean {
	private Integer id;
	private String name;

	public InfoBean() {}

	public InfoBean(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "InfoBean{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
