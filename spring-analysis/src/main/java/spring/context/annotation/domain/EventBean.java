package spring.context.annotation.domain;

import org.springframework.context.ApplicationEvent;

public class EventBean extends ApplicationEvent {

	private String msg;

	public EventBean(Object source, String msg) {
		super(source);
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}
}
