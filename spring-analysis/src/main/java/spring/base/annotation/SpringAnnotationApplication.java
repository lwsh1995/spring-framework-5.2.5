package spring.base.annotation;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import spring.base.annotation.domain.AwareBean;
import spring.base.annotation.domain.InfoBean;

public class SpringAnnotationApplication {

	public static void main(String[] args) {

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		// 注册的两种方式
		context.registerBean("awareBean", AwareBean.class);
		context.scan("spring.base.annotation.config");
		context.refresh();
		InfoBean infoBean = context.getBean("infoBean", InfoBean.class);
		AwareBean awareBean = context.getBean(AwareBean.class);
		context.close();
		System.out.println(infoBean.toString());
		System.out.println(awareBean.toString());
	}
}
