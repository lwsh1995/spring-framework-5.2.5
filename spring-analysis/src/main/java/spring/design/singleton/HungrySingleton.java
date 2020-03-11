package spring.design.singleton;

public class HungrySingleton {

	private final static HungrySingleton instance=new HungrySingleton();

	private HungrySingleton(){}

	public static HungrySingleton getInstance() {
		return instance;
	}
}
