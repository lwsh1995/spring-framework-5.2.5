package spring.base.annotation.domain;

import org.springframework.context.ApplicationEvent;

public class EventBean extends ApplicationEvent {

	private static final long serialVersionUID = -9204067540509007853L;
	private String name;
	public EventBean(Object source) {
		super(source);
		name=source.toString();
	}

	@Override
	public String toString() {
		return "EventBean{" +
				"name='" + name + '\'' +
				'}';
	}

	public void print(){
		System.out.println("event bean print");
	}
}
