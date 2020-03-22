package features;

import java.util.function.Consumer;

public class ConsumerMain {
	public static void main(String[] args) {
		Consumer<String> consumer = p -> System.out.println(p);
		consumer.accept("Consumer Interface");
	}
}
