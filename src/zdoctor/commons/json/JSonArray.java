package zdoctor.commons.json;

import java.util.ArrayList;

public class JSonArray extends ArrayList<JSonValue<?>> {
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
