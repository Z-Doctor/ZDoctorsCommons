package zdoctor.commons.utils;

public class PrimitiveUtil {
	public static Character[] toCharacterArray(String input) {
		return toCharacterArray(input.toCharArray());
	}

	public static Character[] toCharacterArray(char[] input) {
		Character[] output = new Character[input.length];
		for (int i = 0; i < output.length; i++) {
			output[i] = input[i];
		}
		return output;
	}

	public static char[] toCharacterArray(Character[] input) {
		char[] output = new char[input.length];
		for (int i = 0; i < output.length; i++) {
			output[i] = input[i];
		}
		return output;
	}

	public static String toString(Character[] input) {
		return new String(toCharacterArray(input));
	}
}
