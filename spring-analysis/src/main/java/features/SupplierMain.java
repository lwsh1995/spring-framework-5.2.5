package features;

import java.util.function.Supplier;

public class SupplierMain {
	public static void main(String[] args) {
		Supplier<Integer> supplier= () -> 100;
		Integer integer = supplier.get();
		System.out.println(integer);
	}
}
