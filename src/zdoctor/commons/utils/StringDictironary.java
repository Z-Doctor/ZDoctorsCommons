package zdoctor.commons.utils;

import java.util.ArrayList;
import java.util.Collections;

public class StringDictironary extends NodeDictionary<Character, String> {

	protected boolean strictSearch = false;

	public void setStrictSearch(boolean strictSearch) {
		this.strictSearch = strictSearch;
	}

	public String[] searchAll(String search) {
		ArrayList<String> matches = new ArrayList<>();

		Collections.addAll(matches, searchSimilarValues(search));
		Character[][] similarKeys = searchSimilarKeys(PrimitiveUtil.toCharacterArray(search));
		for (Character[] characters : similarKeys) {
			matches.add(PrimitiveUtil.toString(characters));
		}

		if (matches.remove(search)) {
			String match = lookUp(PrimitiveUtil.toCharacterArray(search));
			matches.add(0, match);
		}

		return matches.toArray(new String[0]);
	}

	public String lookUp(String key) {
		return super.lookUp(PrimitiveUtil.toCharacterArray(key));
	}

	public boolean hasKey(String key) {
		return super.hasKey(PrimitiveUtil.toCharacterArray(key));
	}

	public boolean register(String keys, String value) {
		return super.register(PrimitiveUtil.toCharacterArray(keys), value);
	}
	
	@Override
	public Character[][] searchSimilarKeys(Character[] partialKey) {
		if (partialKey == null || partialKey.length <= 0)
			return new Character[0][];

		ArrayList<Character[]> matches = new ArrayList<>(database.keySet());
		matches.removeIf(key -> {
			if (partialKey.length > key.length)
				return true;
			for (int i = 0; i < partialKey.length; i++) {
				if (!strictSearch) {
					if (Character.toLowerCase((char) key[i]) != Character.toLowerCase((char) partialKey[i]))
						return true;
				} else {
					if (!key[i].equals(partialKey[i]))
						return true;
				}
			}
			return false;
		});

		return matches.toArray(new Character[0][]);
	}

	@Override
	public String[] searchSimilarValues(String partialValue) {
		if (partialValue == null || partialValue.length() <= 0)
			return new String[0];

		ArrayList<String> matches = new ArrayList<>(database.values());
		matches.removeIf(key -> {
			if (partialValue.length() > key.length())
				return true;
			if (!strictSearch) {
				if (key.toLowerCase().contains(partialValue.toLowerCase()))
					return false;
			}
			for (int i = 0; i < partialValue.length(); i++) {
				if (!strictSearch) {
					if (Character.toLowerCase(key.charAt(i)) != Character.toLowerCase(partialValue.charAt(i)))
						return true;
				} else {
					if (key.charAt(i) != partialValue.charAt(i))
						return true;
				}
			}
			return false;
		});

		return matches.toArray(new String[0]);
	}

}
