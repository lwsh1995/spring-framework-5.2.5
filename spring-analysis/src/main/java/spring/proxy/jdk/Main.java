package spring.proxy.jdk;

public class Main {
	public static void main(String[] args) {
		UserService userService = new UserServiceImpl();
		ServiceInvocationHandler handler = new ServiceInvocationHandler(userService);
		UserService proxy = (UserService) handler.getProxy();
		proxy.add();
	}
}
