package spring.context.annotation.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BeanModifyBeanFactoryPostProcessor {
	@Value("fuck")
	private String name;

	public String getName() {
		return name;
	}
}
