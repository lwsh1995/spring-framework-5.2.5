package spring.context.annotation.component;

import org.springframework.stereotype.Component;

@Component
public class AopComponent {
	public void aopMethod(){
		System.out.println("aop method run ");
	}
}
