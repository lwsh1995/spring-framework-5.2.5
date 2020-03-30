package spring.context.annotation;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import spring.context.annotation.config.BeanConfig;

public class CustomAnnotationConfigApplicationContext extends AnnotationConfigApplicationContext {
	public CustomAnnotationConfigApplicationContext(Class<?>... componentClasses){
		super(componentClasses);
	}

	@Override
	protected void initPropertySources() {
		getEnvironment().setRequiredProperties("var");
	}

	public static void main(String[] args) {
		CustomAnnotationConfigApplicationContext context = new CustomAnnotationConfigApplicationContext(BeanConfig.class);

	}
}
