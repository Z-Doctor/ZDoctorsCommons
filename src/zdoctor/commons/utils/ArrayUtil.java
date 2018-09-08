package zdoctor.commons.utils;

public class ArrayUtil {
	public static boolean contains(Object[] array, Object... list) {
		for (Object o1 : array)
			for (Object o2 : list)
				if (o1 == o2 || o1.equals(o2))
					return true;
		return false;
	}
}
