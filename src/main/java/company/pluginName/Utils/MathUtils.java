package company.pluginName.Utils;

public class MathUtils {

	public static boolean between(int number, int number1, int number2) {
		return number >= Math.min(number1, number2) && number <= Math.max(number1, number2);
	}

	public static boolean betweenExclusive(int number, int number1, int number2) {
		return number > Math.min(number1, number2) && number < Math.max(number1, number2);
	}
}
