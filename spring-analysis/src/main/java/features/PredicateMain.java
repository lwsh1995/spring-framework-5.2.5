package features;

import java.util.function.Predicate;

public class PredicateMain {
	public static void main(String[] args) {
		Predicate<Integer> predicate= p -> p%2==0;
		boolean test = predicate.test(100);
		System.out.println(test);
	}
}
