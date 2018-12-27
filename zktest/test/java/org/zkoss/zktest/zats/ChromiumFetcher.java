/* ChromiumFetcher.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 07 17:07:04 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rudyhuang
 */
class ChromiumFetcher {
	private static final Logger LOG = LoggerFactory.getLogger(ChromiumFetcher.class);
	private static final String DEFAULT_DOWNLOAD_HOST = "https://storage.googleapis.com";
	private static final Map<Arch, String> URLS = new EnumMap<>(Arch.class);

	static {
		URLS.put(Arch.LINUX, "%s/chromium-browser-snapshots/Linux_x64/%d/%s.zip");
		URLS.put(Arch.MAC, "%s/chromium-browser-snapshots/Mac/%d/%s.zip");
		URLS.put(Arch.WIN32, "%s/chromium-browser-snapshots/Win/%d/%s.zip");
		URLS.put(Arch.WIN64, "%s/chromium-browser-snapshots/Win_x64/%d/%s.zip");
	}

	private final String downloadFolder;
	private final String downloadHost;
	private final Arch platform;

	public enum Arch {
		LINUX, MAC, WIN32, WIN64
	}

	public ChromiumFetcher() {
		this.downloadFolder = System.getProperty("user.home") + "/.m2/repository/chromium/";
		this.downloadHost = DEFAULT_DOWNLOAD_HOST;

		if (SystemUtils.IS_OS_WINDOWS) {
			if (System.getProperty("os.arch").contains("64"))
				this.platform = Arch.WIN64;
			else
				this.platform = Arch.WIN32;
		} else if (SystemUtils.IS_OS_LINUX) {
			this.platform = Arch.LINUX;
		} else if (SystemUtils.IS_OS_MAC) {
			this.platform = Arch.MAC;
		} else {
			throw new IllegalArgumentException("Unsupported platform: " + SystemUtils.OS_NAME);
		}
		LOG.info("Detected arch: {}", this.platform);
	}

	public boolean canDownload(int revision) {
		HttpURLConnection http = null;
		try {
			URL url = getUrl(revision);
			http = (HttpURLConnection) url.openConnection();
			http.connect();
			if (http.getResponseCode() == 200) {
				return true;
			}
		} catch (IOException e) {
			LOG.error("", e);
		} finally {
			if (http != null) http.disconnect();
		}
		return false;
	}

	private URL getUrl(int revision) throws MalformedURLException {
		return new URL(String.format(URLS.get(this.platform), this.downloadHost, revision, archiveName(platform, revision)));
	}

	private String archiveName(Arch platform, int revision) {
		if (platform == Arch.LINUX)
			return "chrome-linux";
		if (platform == Arch.MAC)
			return "chrome-mac";
		if (platform == Arch.WIN32 || platform == Arch.WIN64) {
			// Windows archive name changed at r591479.
			return revision > 591479 ? "chrome-win" : "chrome-win32";
		}
		return null;
	}

	public void download(int revision) throws IOException {
		Path tempZip = null;
		try {
			URL url = getUrl(revision);
			LOG.info("URL: {}", url);
			tempZip = Files.createTempFile("chromium", null);
			downloadFile(url, tempZip);
			extractZip(tempZip, getFolderPath(revision));
			chmod(getExecutablePath(revision));
		} finally {
			deleteFile(tempZip);
		}
	}

	private void downloadFile(URL url, Path dest) throws IOException {
		LOG.info("Download {} to {}", url, dest);
		try (InputStream in = url.openStream()) {
			Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	private void extractZip(Path zip, Path targetDir) throws IOException {
		LOG.info("Extracting {} to {}", zip, targetDir);
		Files.createDirectories(targetDir);
		try (final ZipFile zipFile = new ZipFile(zip.toFile())) {
			Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
			while (entries.hasMoreElements()) {
				final ZipArchiveEntry entry = entries.nextElement();
				final Path file = targetDir.resolve(entry.getName());
				if (entry.isDirectory()) {
					Files.createDirectories(file);
					continue;
				}

				Files.createDirectories(file.getParent());
				if (entry.isUnixSymlink()) {
					Path target = file.resolveSibling(zipFile.getUnixSymlink(entry));
					Files.createSymbolicLink(file, file.getParent().relativize(target));
				} else {
					try (InputStream in = zipFile.getInputStream(entry)) {
						Files.copy(in, file);
						if ((entry.getUnixMode() & 0111) != 0) { // oct(111) means --x--x--x
							file.toFile().setExecutable(true);
						}
					}
				}
			}
		}
		LOG.info("Extracted {} to {}", zip, targetDir);
	}

	public Path getFolderPath(int revision) {
		return Paths.get(this.downloadFolder, this.platform.name() + '-' + revision);
	}

	public Path getExecutablePath(int revision) {
		Path folderPath = getFolderPath(revision);
		Path executablePath;
		switch (this.platform) {
			case MAC:
				executablePath = Paths.get(archiveName(this.platform, revision), "Chromium.app", "Contents", "MacOS", "Chromium");
				break;
			case LINUX:
				executablePath = Paths.get(archiveName(this.platform, revision), "chrome");
				break;
			case WIN32:
			case WIN64:
				executablePath = Paths.get(archiveName(this.platform, revision), "chrome.exe");
				break;
			default:
				throw new IllegalArgumentException("Unsupported platform");
		}
		return folderPath.resolve(executablePath);
	}

	public void chmod(Path path) throws IOException {
		Set<String> supportedAttr = path.getFileSystem().supportedFileAttributeViews();
		if (supportedAttr.contains("posix")) {
			LOG.info("Set {} permission 755", path);
			Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rwxr-xr-x"));
		} else {
			LOG.info("The filesystem is not POSIX-compatible. Ignore.");
		}
	}

	private void deleteFile(Path file) throws IOException {
		Files.deleteIfExists(file);
	}
}
