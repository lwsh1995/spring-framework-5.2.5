package spring.base.annotation.domain;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

public class EventBean extends ApplicationEvent  implements EnvironmentAware {

	private static final long serialVersionUID = -9204067540509007853L;
	private String name;
	private String msg;
	public EventBean(Object source) {
		super(source);
		name=source.toString();
	}


	@Override
	public String toString() {
		return "EventBean{" +
				"name='" + name + '\'' +
				", msg='" + msg + '\'' +
				'}';
	}

	public void print(){
		System.out.println("event bean print");
	}

	@Override
	public void setEnvironment(Environment environment) {
		String property = environment.getProperty("event.name");
		if (property.equals("nCoV"));{
			name=" ****** ";
		}
	}
}
