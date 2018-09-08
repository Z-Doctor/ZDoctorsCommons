package zdoctor.commons.utils;

import java.util.concurrent.TimeUnit;

public class StopWatch {

	protected long startTime;
	protected long stopTime;

	protected boolean running = false;

	public void start() {
		this.startTime = System.nanoTime();
		this.running = true;
	}

	public void stop() {
		this.stopTime = System.nanoTime();
		this.running = false;
	}

	public long getNanos() {
		long elapsed;
		if (running) {
			elapsed = (System.nanoTime() - startTime);
		} else {
			elapsed = (stopTime - startTime);
		}
		return elapsed;
	}

	public long getMillis() {
		long elapsed;
		if (running) {
			elapsed = (System.nanoTime() - startTime);
		} else {
			elapsed = (stopTime - startTime);
		}
		return TimeUnit.MILLISECONDS.convert(elapsed, TimeUnit.NANOSECONDS);
	}

	public long getSeconds() {
		long elapsed;
		if (running) {
			elapsed = (System.nanoTime() - startTime);
		} else {
			elapsed = (stopTime - startTime);
		}
		return TimeUnit.SECONDS.convert(elapsed, TimeUnit.NANOSECONDS);
	}
}