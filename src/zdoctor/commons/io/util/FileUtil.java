package zdoctor.commons.io.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.filechooser.FileNameExtensionFilter;

public class FileUtil {

	public static int bufferSize = 1024 * 1024 * 1024;

	public static final String ILLEGAL_CHARACTERS = "[|?*\"><:]";

	public static final String PATH_REGEX = "(.*)/(.*)";

	public static final String FILENAME_REGEX = ".*/";

	public static String removeInvalidCharacters(String s) {
		return s.replaceAll(ILLEGAL_CHARACTERS, "");
	}

	public static File createFile(String filePath) {
		if (filePath.contains("/")) {
			String path = filePath.replaceFirst(PATH_REGEX, "$1");
			String fileName = filePath.replaceFirst(FILENAME_REGEX, "");
			File folder = new File(path);
			folder.mkdirs();
			return new File(folder, fileName);
		} else
			return new File(filePath);
	}

	public static File createFile(String parentFile, String filePath) {
		return createFile(createFile(parentFile), filePath);
	}

	public static File createFile(File parentFile, String filePath) {
		String fileName = filePath.replaceFirst(FILENAME_REGEX, "");
		return new File(parentFile, fileName);
	}

	public static File createDirs(String path) {
		File dir = new File(path);
		dir.mkdirs();
		return dir;
	}

	public static File createDirs(File parentFolder, String path) {
		File dir = new File(parentFolder, path);
		dir.mkdirs();
		return dir;
	}

	public static File flushToFile(String file, byte[] data) {
		return flushToFile(createFile(file), data);
	}

	public static File flushToFile(File file, byte[] data) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(data);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	public static File flushToTempFile(byte[] data) throws IOException {
		File temp = File.createTempFile("temp", "dat");
		temp.deleteOnExit();
		return flushToFile(temp, data);
	}

	public static byte[] getFileData(String file) {
		return getFileData(createFile(file));
	}

	public static byte[] getFileData(File file) {
		ByteArrayOutputStream data = new ByteArrayOutputStream();

		try {
			InputStream is = new FileInputStream(file);
			byte[] buffer = new byte[bufferSize];
			int read = 0;
			while ((read = is.read(buffer)) > 0) {
				data.write(buffer, 0, read);
			}
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return data.toByteArray();
	}

	public static File enforceFileExt(File file, FileNameExtensionFilter extFilter) {
		boolean hasExt = false;
		String[] extList = extFilter.getExtensions();
		for (String ext : extList) {
			if (file.getName().endsWith(ext)) {
				hasExt = true;
				break;
			}
		}

		if (!hasExt)
			file = new File(file.getName() + "." + extList[0]);

		return file;
	}

}
