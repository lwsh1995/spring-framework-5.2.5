package spring.context.annotation.component;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import spring.context.annotation.domain.EventBean;

@Component
public class SpringApplicationListener implements ApplicationListener<EventBean> {
	@Override
	public void onApplicationEvent(EventBean event) {
		System.out.println("listener "+event.getMsg());
	}
}
