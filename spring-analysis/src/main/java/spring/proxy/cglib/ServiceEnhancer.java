package spring.proxy.cglib;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class ServiceEnhancer {
	public static void main(String[] args) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(ServiceEnhancer.class);
		enhancer.setCallback(new MethodInterceptorImpl());
		ServiceEnhancer serviceEnhancer = (ServiceEnhancer) enhancer.create();
		serviceEnhancer.add();
		serviceEnhancer.test();
	}

	public void add(){
		System.out.println("--- add ---");
	}

	public void test(){
		System.out.println("--- test ---");
	}

	private static class MethodInterceptorImpl implements MethodInterceptor{

		@Override
		public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
			System.out.println("before invoke "+method);
			Object result = methodProxy.invokeSuper(o, objects);
			System.out.println("after invoke "+method);
			return result;
		}
	}
}
