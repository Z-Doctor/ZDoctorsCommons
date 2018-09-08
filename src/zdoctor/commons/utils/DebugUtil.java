package zdoctor.commons.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

public class DebugUtil {
	public static void notifyUser(String format, Object... args) {
		JOptionPane.showMessageDialog(null, String.format(format, args));
	}

	public static void notifyUser(String title, String format, Object... args) {
		JOptionPane.showMessageDialog(null, String.format(format, args), title, JOptionPane.PLAIN_MESSAGE);
	}

	public static void warnUser(String title, String format, Object... args) {
		JOptionPane.showMessageDialog(null, String.format(format, args), title, JOptionPane.WARNING_MESSAGE);
	}

	public static Debug createLog(String fileName) {
		return new Debug(fileName);
	}

	public static class Debug {

		FileWriter log;
		private File file;
		private boolean append;

		public Debug(String fileName) {
			this(new File(fileName));
		}

		public Debug(File file) {
			this(file, true);
		}

		public Debug(File file, boolean append) {
			try {
				this.file = file;
				log = new FileWriter(file, append);
				this.append = append;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void append(String message) {
			try {
				log.write(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void appendLine(String message) {
			append(message);
			append("\n");
		}

		public void flush() {
			try {
				log.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void clear() {
			try {
				log.close();
				log = new FileWriter(file);
				log.close();
				log = new FileWriter(file, append);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
