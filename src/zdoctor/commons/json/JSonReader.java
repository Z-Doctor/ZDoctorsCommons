package zdoctor.commons.json;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

	public JSonObject readObject() {
		try {
			return json.parseJsonObject(input);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public JSonArray readArray() {
		try {
			return json.parseJsonArray(input);
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

//		if (charArray[0] != '{')
//			throw new IOException("Invalid start of Json expected '{' got '" + charArray[0] + "'");
//		if (charArray[charArray.length - 1] != '}')
//			throw new IOException("Invalid end of Json expected '}' got '" + charArray[charArray.length - 1] + "'");
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
}
