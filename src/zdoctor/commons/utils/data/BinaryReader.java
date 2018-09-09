package zdoctor.commons.utils.data;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

import zdoctor.commons.utils.ArrayUtil;

public class BinaryReader implements Closeable {
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	protected final byte[] SHORT = new byte[2];
	protected final byte[] INT = new byte[4];
	protected final byte[] LONG = new byte[8];

	protected long position;

	protected long cacheLength;

	protected byte[][] cache;

	protected final long dataSize;

	protected ByteOrder defaultOrder = ByteOrder.nativeOrder();

	protected int bufferSize = 100000000;

	protected InputStream reader;

	protected static int longToInt(long l) {
		if (l < 0)
			return -1;
		if (l == 0)
			return 0;

		int longInt = (int) (l % (MAX_ARRAY_SIZE));
		return longInt == 0 ? MAX_ARRAY_SIZE : longInt;
	}

	public BinaryReader(File file) throws IOException {
		reader = new FileInputStream(file);
		dataSize = file.length();
		setupCache();
	}

	public BinaryReader(String fileName) throws IOException {
		reader = new FileInputStream(fileName);
		dataSize = ((FileInputStream) reader).getChannel().size();
		setupCache();
	}

	public BinaryReader(FileDescriptor fileDescriptor) throws IOException {
		reader = new FileInputStream(fileDescriptor);
		dataSize = ((FileInputStream) reader).getChannel().size();
		setupCache();
	}

	public BinaryReader(InputStream ios, long dataSize) throws IOException {
		reader = ios;
		this.dataSize = dataSize;
		setupCache();
	}

	public BinaryReader(byte[] data) throws IOException {
		reader = new ByteArrayInputStream(data);
		this.dataSize = data.length;
		setupCache();
	}

	protected void setupCache() throws IOException {
		int cachePartions = (int) (dataSize / MAX_ARRAY_SIZE + 1);
		cache = new byte[cachePartions][];

		long temp = dataSize;
		for (int i = 0; i < cachePartions; i++) {
//				System.out.println(i);
			cache[i] = new byte[(int) (temp > MAX_ARRAY_SIZE ? MAX_ARRAY_SIZE : temp)];
			temp -= MAX_ARRAY_SIZE;
		}
	}

	public int cacheIndex() {
		return (int) (position() / MAX_ARRAY_SIZE);
	}

	public long position() {
		if (position < 0)
			position = 0;
		return position;
	}

	public long position(long pos) {
		if (pos > dataSize)
			pos = dataSize;
		if (pos < 0)
			pos = 0;

		if (pos > cacheLength)
			seek(pos);
		else
			position = pos;

		return position();
	}

	public long seek(long pos) {
		if (pos > dataSize)
			pos = dataSize;
		if (pos < 0)
			pos = 0;

		if (pos <= cacheLength)
			position(pos);
		else {
			long read = pos - position();
//			System.out.println("To Read: " + read);
			byte[] buff = new byte[bufferSize];
			while (read > 0) {
				try {
					read(buff, 0, (int) (read > buff.length ? buff.length : read));
					read -= buff.length;
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}

		return position();
	}

	public void setBufferSize(int size) {
		if (size > 0)
			bufferSize = size;
	}

	public String readLine() {
		String newLine = System.getProperty("line.separator");
		if (newLine.length() > 1) {
			StringBuilder sb = new StringBuilder();
			try {
				while (available() > 0) {
					int read = read();
					if (read == 0 || read == -1)
						break;
					else if (read == newLine.charAt(newLine.length() - 1)) {
						sb.delete(sb.length() - (newLine.length() - 1), sb.length());
						break;
					}
					sb.append((char) read);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return sb.toString();
		} else
			return readLine(newLine.charAt(0), (char) 0);
	}

	public long available() {
		return dataSize - position();
	}

	public String readLine(Character... escapeChars) {
		StringBuilder sb = new StringBuilder();
		try {
			while (available() > 0) {
				int read = read();
				if (ArrayUtil.contains(escapeChars, (char) read) || read == -1)
					break;
				sb.append((char) read);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public String readString(Character... ignore) {
		String newLine = System.getProperty("line.separator");
		if (newLine.length() > 1) {
			StringBuilder sb = new StringBuilder();
			try {
				while (available() > 0) {
					int read = read();

					if (!ArrayUtil.contains(ignore, read)
							&& (read == 0 || read == -1 || !Character.isLetterOrDigit(read)))
						break;
					else if (read == newLine.charAt(newLine.length() - 1)) {
						sb.delete(sb.length() - (newLine.length() - 1), sb.length());
						break;
					}
					sb.append((char) read);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return sb.toString();
		} else
			return readLine(newLine.charAt(0), (char) 0, ' ');
	}

	public String readString() {
		String newLine = System.getProperty("line.separator");
		if (newLine.length() > 1) {
			StringBuilder sb = new StringBuilder();
			try {
				while (available() > 0) {
					int read = read();
					if (read == 0 || read == -1 || !Character.isLetterOrDigit(read))
						break;
					else if (read == newLine.charAt(newLine.length() - 1)) {
						sb.delete(sb.length() - (newLine.length() - 1), sb.length());
						break;
					}
					sb.append((char) read);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return sb.toString();
		} else
			return readLine(newLine.charAt(0), (char) 0, ' ');
	}

	public String readString(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			try {
				sb.append((char) read());
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
		return sb.toString();
	}

	public long availableCache() {
		return cacheLength - position();
	}

	public void overrideCache(byte... bytes) {
		for (byte b : bytes) {
			cache[cacheIndex()][longToInt(position++)] = b;
		}
	}

	public void addToCache(byte... bytes) {
		for (byte b : bytes) {
			cache[cacheIndex()][getCachePosition()] = b;
			position++;
			cacheLength++;
		}
	}

	protected int getCachePosition() {
//		System.out.println(Integer.MAX_VALUE - MAX_ARRAY_SIZE);
		return (int) (position() - (cacheIndex() * MAX_ARRAY_SIZE));
	}

	public void setByteOrder(ByteOrder order) {
		defaultOrder = order;
	}

	public short readShort() throws IOException {
		return readShort(defaultOrder);
	}

	public int readInt() throws IOException {
		return readInt(defaultOrder);
	}

	public long readLong() throws IOException {
		return readLong(defaultOrder);
	}

	public short readShort(ByteOrder order) throws IOException {
		read(SHORT);
		StringBuilder sb = new StringBuilder();
		for (byte b : SHORT) {
			sb.append(Integer.toBinaryString(b));
		}
		short read = Short.parseShort(sb.toString(), 2);
		if (order == ByteOrder.BIG_ENDIAN)
			return read;
		return Short.reverseBytes(read);
	}

	public int readInt(ByteOrder order) throws IOException {
		read(INT);
		StringBuilder sb = new StringBuilder();
		for (byte b : INT) {
			sb.append(Integer.toBinaryString(b));
		}
		int read = Integer.parseInt(sb.toString(), 2);
		if (order == ByteOrder.BIG_ENDIAN)
			return read;
		return Integer.reverseBytes(read);
	}

	public long readLong(ByteOrder order) throws IOException {
		read(LONG);
		StringBuilder sb = new StringBuilder();
		for (byte b : LONG) {
			sb.append(Long.toBinaryString(b));
		}
		Long read = Long.parseLong(sb.toString(), 2);
		if (order == ByteOrder.BIG_ENDIAN)
			return read;
		return Long.reverseBytes(read);
	}

	public void readAll() {
		seek(dataSize - 1);
	}

	public byte[] read(int length) throws IOException {
		byte[] temp = new byte[length];
		read(temp);
		return temp;
	}

	public long skip(long n) throws IOException {
		position += n;
		cacheLength += n;
		return reader.skip(n);
	}

	public int peek() throws IOException {
		int read = read();
		position -= 1;
		return read;
	}

	public int peekBack() throws IOException {
		position -= 1;
		int read = read();
		return read;
	}

	public int read() throws IOException {
		if (position() >= cacheLength) {
//			System.out.println("Read");
			int read = reader.read();
			addToCache((byte) read);
			return read;
		} else {
//			System.out.println("cache");
			return cache[cacheIndex()][longToInt(position++)];
		}
	}

	public int read(byte[] b) throws IOException {
		if (position() >= cacheLength) {
			int read = reader.read(b);
			for (int i = 0; i < read; i++) {
				cache[cacheIndex()][longToInt(position++)] = b[i];
				cacheLength++;
			}
			return read;
		} else {
			if (availableCache() > b.length)
				return read(b, 0, b.length);
			else {
				int read = (int) availableCache();
				return read(b, 0, read) + read(b, read, b.length - read);
			}
		}
	}

	public int read(byte[] b, int off, int len) throws IOException {
		if (position() >= cacheLength) {
			int read = reader.read(b, off, len);
			for (int i = off; i < read; i++) {
				addToCache(b[i]);
			}
			return read;
		} else {
			if (availableCache() > len) {
				for (int i = off; i < len; i++) {
					b[i] = (byte) read();
				}
				return len;
			} else {
				int read = (int) availableCache();
				for (int i = off; i < read; i++) {
					b[i] = (byte) read();
				}
				return read + read(b, read, len - read);
			}
		}
	}

	public void close() throws IOException {
		reader.close();
	}

}
