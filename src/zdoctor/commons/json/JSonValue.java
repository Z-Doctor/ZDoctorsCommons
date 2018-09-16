package zdoctor.commons.json;

public class JSonValue<T> {
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

	public static enum ValueType {
		BOOLEAN, Number, STRING, ARRAY, OBJECT, NULL
	}
}
