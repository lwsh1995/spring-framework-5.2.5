package spring.base.annotation;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import spring.base.annotation.domain.User;

public class SpringAnnotationApplication {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.scan("spring.base.annotation.config");
		context.refresh();
		User user = context.getBean("user", User.class);
		System.out.println(user.toString());
	}
}
