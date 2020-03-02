package spring.base.annotation;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import spring.base.annotation.domain.Info;

public class SpringAnnotationApplication {

	public static void main(String[] args) {

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		// 注册的两种方式
		context.registerBean("info2",Info.class,"spring 2",2);
		context.scan("spring.base.annotation.config");
		context.refresh();
		Info info1 = context.getBean("info1", Info.class);
		Info info2 = context.getBean("info2", Info.class);
		System.out.println(info1.toString()+info2.toString());
	}
}
