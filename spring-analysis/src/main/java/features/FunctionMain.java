package features;

import java.util.function.Function;

public class FunctionMain {
	public static void main(String[] args) {
		Function<Integer,Integer>  func= p -> p*10;
		Integer result = func.apply(10);
		System.out.println(result);
	}
}
