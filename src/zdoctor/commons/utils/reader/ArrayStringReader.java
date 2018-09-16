package zdoctor.commons.utils.reader;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ArrayStringReader {
	private ArrayList<Object> items = new ArrayList<>();

	public ArrayStringReader(String input) {
		input = input.trim();
		if (!input.startsWith("[")) {
			System.out.println("Invalid Start");
			return;
		}
		if (!input.endsWith("]")) {
			System.out.println("Invalid end");
			return;
		}
		if (!validateArrays(input.toCharArray())) {
			System.out.println("Invalid array");
			return;
		}
		if (!validateStrings(input.toCharArray())) {
			System.out.println("Invalid String");
			return;
		}

		parseArray(input, 0, items);
	}

	public void forEach(Consumer<Object> action) {
		items.forEach(action);
	}

	private boolean validateStrings(char[] charArray) {
		int quote = 0;
		for (int i = 0; i < charArray.length; i++) {
			char c = charArray[i];
			if (c == '"')
				quote++;
		}
		return quote % 2 == 0;
	}

	private boolean validateArrays(char[] charArray) {
		int open = 0, close = 0;
		for (int i = 0; i < charArray.length; i++) {
			char c = charArray[i];
			if (c == '[')
				open++;
			else if (c == ']')
				close++;
		}
		return open == close;
	}

	private static String parseString(String input, int start) {
		char[] charArray = input.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = start; i < charArray.length - 1; i++) {
			char c = charArray[i];
			if (c == '"') {
				if (i == start)
					continue;
				else
					return sb.toString();
			} else {
				sb.append(c);
			}
		}
		return "Error: No End of String";
	}

	public static int parseArray(String input, int start, ArrayList<Object> list) {
		input = input.trim();
		char[] charArray = input.toCharArray();
		StringBuilder buffer = new StringBuilder();
		for (int i = start; i < charArray.length; i++) {
			char c = charArray[i];
			if (c == '[' && i != start) {
				ArrayList<Object> temp = new ArrayList<>();
				i += parseArray(input, i, temp);
				list.add(temp);
			} else if (c == '"') {
				String s = parseString(input, i);
				list.add(s);
				i += s.length() + 1;
			} else if (c == ',' || c == ']') {
				if (buffer.length() > 0) {
					if (buffer.indexOf(".") != -1) {
						double d = Double.parseDouble(buffer.toString());
						list.add(d);
					} else {
						long l = Long.parseLong(buffer.toString());
						list.add(l);
					}
					buffer = new StringBuilder();
				}
				if (c == ']' && i != charArray.length - 1) {
					return i - start;
				}
			} else if (Character.isDigit(c) || c == '.')
				buffer.append(c);
		}

		return charArray.length;
	}
}
