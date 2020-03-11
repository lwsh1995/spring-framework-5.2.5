package spring.base.annotation;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import spring.base.annotation.config.AppConfig;
import spring.base.annotation.domain.StaticValue;
import spring.base.annotation.domain.*;

import java.math.BigDecimal;

public class SpringAnnotationApplication {

	public static void main(String[] args) throws Exception {


/*		第一种方式
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		// 注册的两种方式
		context.registerBean("awareBean", AwareBean.class);
		context.scan("spring.base.annotation.config");
		context.refresh();*/
		//第二种方式
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		InfoBean infoBean = context.getBean("&infoBean", InfoBean.class);
		System.out.println(infoBean.toString());
		CustomizeFactoryBean customizeFactoryBean = context.getBean("infoBean", CustomizeFactoryBean.class);
		System.out.println(customizeFactoryBean.toString());

		AwareBean awareBean = context.getBean(AwareBean.class);
		System.out.println(awareBean.toString());

		StaticValue bean = context.getBean(StaticValue.class);
		System.out.println("bean  "+bean.getValue());

		EventBean eventBean = context.getBean(EventBean.class);
		System.out.println(eventBean);

		context.publishEvent(new EventBean("event ----"));

	}

}
