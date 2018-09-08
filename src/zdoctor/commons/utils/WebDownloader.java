package zdoctor.commons.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zdoctor.commons.utils.data.FileUtil;

public class WebDownloader {

	public final static Pattern URL_PATTERN;
	public final static String URL_BASE = "http.*://[A-z0-9.-]*/";

	static {
		String urlRegex = "^(http|https)://[-a-zA-Z0-9+&@#/%?=~_|,!:.;]*[-a-zA-Z0-9+@#/%=&_|]";
		URL_PATTERN = Pattern.compile(urlRegex);
	}

	public static boolean ValidateUrl(String url) {
		Matcher matcher = URL_PATTERN.matcher(url);
		return matcher.matches();
	}

	public static String CheckUrlPrefix(String url) {
		if (!url.startsWith("http"))
			url = "http://" + url;
		return url;
	}

	private boolean valid;
	private File downloadFolder;

	public WebDownloader(File downloadFolder) {
		this.downloadFolder = downloadFolder;
		validate();
	}

	public void validate() {
		if (!downloadFolder.isDirectory())
			valid = false;
		else
			valid = true;
	}

	public File downloadToFile(String url) {
		if (valid && !ValidateUrl(url) && !ValidateUrl(url = CheckUrlPrefix(url)))
			return null;
		File result = null;
		try {
			URL website = new URL(url);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(
					(result = FileUtil.createFile(downloadFolder, url.replaceFirst(URL_BASE, ""))));
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			rbc.close();
		} catch (IOException e) {
			return null;
		}

		return result;
	}

	public boolean download(String url) {
		if (valid && !ValidateUrl(url) && !ValidateUrl(url = CheckUrlPrefix(url)))
			return false;
		try {
			URL website = new URL(url);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(
					FileUtil.createFile(downloadFolder, url.replaceFirst(URL_BASE, "")));
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			rbc.close();
		} catch (IOException e) {
			return false;
		}

		return true;
	}

	public boolean download(String url, String parentFolderPath) {
		if (valid && !ValidateUrl(url) && !ValidateUrl(url = CheckUrlPrefix(url)))
			return false;
		try {
			File parentFolder = FileUtil.createDirs(downloadFolder, parentFolderPath);

			URL website = new URL(url);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(
					FileUtil.createFile(parentFolder, url.replaceFirst(URL_BASE, "")));
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			rbc.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public boolean download(String url, long endOffset) {
		if (valid && !ValidateUrl(url) && !ValidateUrl(url = CheckUrlPrefix(url)))
			return false;
		try {
			URL website = new URL(url);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(
					FileUtil.createFile(downloadFolder, url.replaceFirst(URL_BASE, "")));
			fos.getChannel().transferFrom(rbc, 0, endOffset);
			fos.close();
			rbc.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public boolean download(String url, long startOffset, long endOffset) {
		if (valid && !ValidateUrl(url) && !ValidateUrl(url = CheckUrlPrefix(url)))
			return false;
		try {
			URL website = new URL(url);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(
					FileUtil.createFile(downloadFolder, url.replaceFirst(URL_BASE, "")));
			fos.getChannel().transferFrom(rbc, startOffset, endOffset);
			fos.close();
			rbc.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public boolean resumeDownload(String url, File exisitingDownload) {
		if (valid && exisitingDownload.exists() && !ValidateUrl(url) && !ValidateUrl(url = CheckUrlPrefix(url)))
			return false;
		try {
			URL website = new URL(url);

			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(exisitingDownload, true);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			rbc.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public static byte[] downloadToArray(String url) throws IOException {
		URL website = new URL(url);
		InputStream inputStream = website.openStream();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int read = 0;
		while ((read = inputStream.read(buffer)) > 0) {
			bos.write(buffer, 0, read);
		}
		inputStream.close();
		bos.close();
		return bos.toByteArray();
	}

}
