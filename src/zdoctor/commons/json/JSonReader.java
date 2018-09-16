package zdoctor.commons.json;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class JSonReader {
	private JSonObject json = new JSonObject();
	private String input;

	public JSonReader(File inputFile) throws IOException {
		FileReader fr = new FileReader(inputFile);
		char[] data = new char[(int) inputFile.length()];
		fr.read(data);
		fr.close();
		String input = new String(data).trim();
		validate(input);
		this.input = input;
	}

	public JSonReader(String input) throws IOException {
		input = input.trim();
		validate(input);
		this.input = input;
	}

	public JSonObject read() {
		try {
			return json.parseJson(input);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void validate(String input) throws IOException {
		int openO = 0, closeO = 0, openA = 0, closeA = 0, quote = 0;
		char[] charArray = input.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			char c = charArray[i];
			if (c == '{')
				openO++;
			else if (c == '}')
				closeO++;
			else if (c == '[')
				openA++;
			else if (c == ']')
				closeA++;
			else if (c == '"')
				quote++;
		}

		if (charArray[0] != '{')
			throw new IOException("Invalid start of Json expected '{' got '" + charArray[0] + "'");
		if (charArray[charArray.length - 1] != '}')
			throw new IOException("Invalid end of Json expected '}' got '" + charArray[charArray.length - 1] + "'");
		if (openO != closeO)
			throwAndFindObject(input);
		if (openA != closeA)
			throwAndFindArray(input);
		if (quote % 2 != 0)
			throwAndFindQuote(input);
	}

	private void throwAndFindObject(String input) throws IOException {
		throw new IOException("Unclosed object");
	}

	private void throwAndFindArray(String input) throws IOException {
		throw new IOException("Unclosed array");
	}

	private void throwAndFindQuote(String input) throws IOException {
		throw new IOException("Unclosed String");
	}

	public static class JSonObject {
		HashMap<String, JSonValue<?>> jsonValues = new HashMap<>();
		private String input;

		@Override
		public String toString() {
			return jsonValues.toString();
		}

		public String getInput() {
			return input;
		}

		public HashMap<String, JSonValue<?>> getJsonValues() {
			return new HashMap<>(jsonValues);
		}

		public JSonValue<?> get(String key) {
			return getJsonValues().get(key);
		}

		public long getNumber(String key) {
			return getJsonValues().get(key).getNumber();
		}

		public double getDecimal(String key) {
			return getJsonValues().get(key).getDecimal();
		}

		public String getString(String key) {
			return getJsonValues().get(key).getString();
		}

		public boolean getBoolean(String key) {
			return getJsonValues().get(key).getBoolean();
		}

		public JSonObject getObject(String key) {
			return getJsonValues().get(key).getObject();
		}

		public JSonArray getArray(String key) {
			return getJsonValues().get(key).getArray();
		}

		public void forEach(BiConsumer<String, JSonValue<?>> action) {
			getJsonValues().forEach(action);
		}

		protected JSonObject parseJson(String input) throws IOException {
			input = input.trim();
			this.input = input;

			char[] charArray = input.toCharArray();
			int start = -1;
			for (int i = 0; i < charArray.length; i++) {
				char c = charArray[i];
				if (c == '{') {
					start = i;
					break;
				}
			}

			if (start == -1) {
				System.out.println("Could not find start of object");
				return null;
			}

			parseObject(input, start, this);

			return this;
		}

		protected static int nextChar(String input, int start) {
			char[] charArray = input.toCharArray();
			for (int i = start; i < charArray.length - 1; i++) {
				char c = charArray[i];
				if (Character.isWhitespace(c)) {
					continue;
				} else if (i != start) {
//					System.out.println("Char found: " + c);
					return i;
				}
			}
			return charArray.length - start;
		}

		protected static int parseObject(String input, int start, JSonObject json) throws IOException {
			String key = "";
			char[] charArray = input.toCharArray();
			for (int i = start; i < charArray.length; i++) {
				char c = charArray[i];
				if (c == '{' && i != start) {
					JSonObject temp = new JSonObject();
					i = parseObject(input, i, temp);
					addValue(json, key, new JSonValue<JSonObject>(ValueType.OBJECT, temp));
					key = "";
				} else if (c == '"') {
					String s = parseString(input, i);
					i += s.length() + 1;
					int j = nextChar(input, i);
					if (charArray[j] == ':') {
						key = s;
					} else {
						addValue(json, key, new JSonValue<String>(ValueType.STRING, s));
						key = "";
					}
				} else if (c == '[') {
					JSonArray temp = new JSonArray();
					i = parseArray(input, i, json, temp);
					addValue(json, key, new JSonValue<JSonArray>(ValueType.ARRAY, temp));
					key = "";
				} else if (Character.isDigit(c)) {
					String num = parseNumber(input, i);
					i += num.length();
					if (num.contains("."))
						addValue(json, key, new JSonValue<Double>(ValueType.Number, Double.parseDouble(num)));
					else
						addValue(json, key, new JSonValue<Long>(ValueType.Number, Long.parseLong(num)));
				} else if (Character.isLetter(c)) {
					i = parseOther(input, i, json, key);
				} else if (c == '}')
					return i;
			}

			return charArray.length - 1;
		}

		protected static int parseOther(String input, int start, JSonObject json, String key) throws IOException {
			char[] charArray = input.toCharArray();
			StringBuilder sb = new StringBuilder();
			for (int i = start; i < charArray.length - 1; i++) {
				char c = charArray[i];
				if (Character.isLetter(c))
					sb.append(c);
				else
					break;
			}
			String value = sb.toString();
			if (value.equalsIgnoreCase("NULL")) {
				addValue(json, key, new JSonValue<Object>(ValueType.NULL, null));
			} else if (value.equalsIgnoreCase("True") || value.equalsIgnoreCase("False")) {
				addValue(json, key, new JSonValue<Boolean>(ValueType.BOOLEAN, Boolean.valueOf(value)));
			} else {
				throw new IOException("Unable to parse value of '" + value + "'");
			}
			return value.length();
		}

		protected static void addValue(JSonObject json, String key, JSonValue<?> jSonValue) {
			if (key.equals(""))
				try {
					throw new IOException("Key is empty for an array, this is a problem");
				} catch (IOException e) {
					e.printStackTrace();
				}
			else
				json.jsonValues.put(key, jSonValue);
		}

		protected static int parseArray(String input, int start, JSonObject json, JSonArray jsonArray)
				throws IOException {
			char[] charArray = input.toCharArray();
			String key = "";
			for (int i = start; i < charArray.length; i++) {
				char c = charArray[i];
				if (c == '[' && i != start) {
					JSonArray temp = new JSonArray();
					i = parseArray(input, i, json, temp);
					addValue(json, key, new JSonValue<JSonArray>(ValueType.ARRAY, temp));
					key = "";
				} else if (c == '"') {
					String s = parseString(input, i);
					i += s.length() + 1;
					int j = nextChar(input, i);
					if (charArray[j] == ':') {
						key = s;
						System.out.println("New Key: " + key);
					} else {
						jsonArray.add(new JSonValue<String>(ValueType.STRING, s));
						key = "";
					}
				} else if (c == ']')
					return i;
				else if (c == '{') {
					JSonObject temp = new JSonObject();
					i = parseObject(input, i, temp);
					jsonArray.add(new JSonValue<JSonObject>(ValueType.OBJECT, temp));
					key = "";
				} else if (Character.isDigit(c)) {
					String num = parseNumber(input, i);
					i += num.length();
					if (num.contains("."))
						jsonArray.add(new JSonValue<Double>(ValueType.Number, Double.parseDouble(num)));
					else
						jsonArray.add(new JSonValue<Long>(ValueType.Number, Long.parseLong(num)));
				} else if (Character.isLetter(c)) {
					i = parseOther(input, i, json, key);
				}

			}

			return charArray.length - 1;
		}

		protected static String parseString(String input, int start) throws IOException {
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
			throw new IOException("No End of String");
		}

		protected static String parseNumber(String input, int start) {
			char[] charArray = input.toCharArray();
			StringBuilder sb = new StringBuilder();
			for (int i = start; i < charArray.length - 1; i++) {
				char c = charArray[i];
				if (Character.isDigit(c) || c == '.')
					sb.append(c);
				else
					return sb.toString();
			}
			return sb.toString();
		}

	}

	public static class JSonArray extends ArrayList<JSonValue<?>> {
		private static final long serialVersionUID = 2861442472777821209L;

		public long getNumber(int index) {
			return get(index).getNumber();
		}

		public double getDecimal(int index) {
			return get(index).getDecimal();
		}

		public String getString(int index) {
			return get(index).getString();
		}

		public boolean getBoolean(int index) {
			return get(index).getBoolean();
		}

		public JSonObject getObject(int index) {
			return get(index).getObject();
		}

		public JSonArray getArray(int index) {
			return get(index).getArray();
		}
	}

	public static class JSonValue<T> {
		private ValueType type;
		private T value;

		public JSonValue(ValueType type, T value) {
			this.type = type;
			this.value = value;
		}

		public T getValue() {
			return value;
		}

		public ValueType getType() {
			return type;
		}

		@Override
		public String toString() {
			return value.toString();
		}

		public long getNumber() {
			return (long) getValue();
		}

		public double getDecimal() {
			return (double) getValue();
		}

		public String getString() {
			return (String) getValue();
		}

		public boolean getBoolean() {
			return (boolean) getValue();
		}

		public JSonObject getObject() {
			return (JSonObject) getValue();
		}

		public JSonArray getArray() {
			return (JSonArray) getValue();
		}

	}

	public static enum ValueType {
		BOOLEAN, Number, STRING, ARRAY, OBJECT, NULL
	}
}
