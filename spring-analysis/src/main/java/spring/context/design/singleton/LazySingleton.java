package spring.context.design.singleton;

public class LazySingleton {
	private static LazySingleton lazySingleton;
	private LazySingleton(){}
	public static LazySingleton getLazySingleton() {
		if (lazySingleton==null){
			synchronized (LazySingleton.class){
				if (lazySingleton==null){
					lazySingleton=new LazySingleton();
				}
			}
		}
		return lazySingleton;
	}
}
