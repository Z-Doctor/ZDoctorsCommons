package zdoctor.commons.utils;

import java.util.function.Consumer;
import java.util.function.Function;

public class Util {
	public static void loopRange(String rangeString, Function<Integer, Boolean> function) {
		rangeString = rangeString.replaceAll(" ", "");
		String[] rangeList = rangeString.split(",");
		for (String range : rangeList) {
			String[] indexs = range.split("-");
			if (indexs.length == 1) {
				function.apply(Integer.parseInt(indexs[0]));
				function.apply(-1);
			} else if (indexs.length == 2) {
				int min = Math.min(Integer.parseInt(indexs[0]), Integer.parseInt(indexs[1]));
				int max = Math.max(Integer.parseInt(indexs[0]), Integer.parseInt(indexs[1]));
				for (int i = min; i <= max; i++)
					if (function.apply(i))
						break;
				function.apply(-1);
			}
		}
	}

	public static int countRange(String loopParams) {
		loopParams = loopParams.replaceAll(" ", "");
		String[] rangeList = loopParams.split(",");
		int count = 0;
		for (String range : rangeList) {
			String[] indexs = range.split("-");
			if (indexs.length == 1) {
				count++;
			} else if (indexs.length == 2) {
				int min = Math.min(Integer.parseInt(indexs[0]), Integer.parseInt(indexs[1]));
				int max = Math.max(Integer.parseInt(indexs[0]), Integer.parseInt(indexs[1]));
				for (int i = min; i <= max; i++)
					count++;
			}
		}
		return count;
	}

	public static void repeat(int loops, Consumer<Integer> consumer) {
		for (int i = 0; i < loops; i++) {
			consumer.accept(i);
		}
	}

	public static Runnable run(Runnable run) {
		run.run();
		return run;
	}

	public static void runThread(Runnable run) {
		Thread thread = new Thread(run);
		thread.start();
	}
}
