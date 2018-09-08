package zdoctor.commons.utils.data;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtil implements Closeable {

	public static int bufferSize = 1024;

	protected ZipFile zip;

	public ZipUtil(ZipFile zip) {
		this.zip = zip;
	}

	public void writeToZip(String entry, byte[] data) {

	}

	public byte[] extractEntry(ZipEntry entry) {
		return extractEntry(zip, entry);
	}

	public static byte[] extractEntry(ZipFile zip, ZipEntry entry) {
		try {
//			System.out.println(entry.getName());
			InputStream zis = zip.getInputStream(entry);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[bufferSize];
			int read = 0;
			while ((read = zis.read(buffer)) > 0) {
				bos.write(buffer, 0, read);
			}
			zis.close();
			bos.close();
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new byte[0];
	}

	public static void writeToZip(ZipOutputStream zos, String entry, byte[] data) {
		try {
			zos.putNextEntry(new ZipEntry(entry));
			zos.write(data);
			zos.closeEntry();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws IOException {
		zip.close();
	}

	public Enumeration<? extends ZipEntry> entries() {
		return zip.entries();
	}

}
