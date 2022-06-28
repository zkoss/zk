/* ChromiumHeadlessDriver.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 07 16:16:29 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A local running Chromium ChromeDriver. By default it runs in headless mode.
 *
 * @author rudyhuang
 */
public class ChromiumHeadlessDriver extends ChromeDriver {
	private static final Logger LOG = LoggerFactory.getLogger(ChromiumHeadlessDriver.class);
	private static final String CHROME_DRIVER_VERSION = "103.0.5059.0";
	private static final int CHROMIUM_BINARY_REVISION = 1002410; // from puppeteer v14.2.0 Chromium 103.0.5059.0 (r1002410)


	static {
		WebDriverManager.chromiumdriver().driverVersion(CHROME_DRIVER_VERSION).setup();
		System.setProperty("webdriver.chrome.logfile", String.format("%s/chromedriver.log", System.getProperty("java.io.tmpdir")));
		System.setProperty("webdriver.chrome.verboseLogging", "true");
	}

	public ChromiumHeadlessDriver() {
		this(true);
	}

	public ChromiumHeadlessDriver(boolean headless) {
		this(new ChromeOptions(), headless);
	}

	public ChromiumHeadlessDriver(ChromeOptions options) {
		this(options, true);
	}

	public ChromiumHeadlessDriver(ChromeOptions options, boolean headless) {
		super(headlessSettings(options, headless));
	}

	public ChromiumHeadlessDriver(ChromeDriverService service) {
		this(service, new ChromeOptions());
	}

	public ChromiumHeadlessDriver(ChromeDriverService service, ChromeOptions options) {
		this(service, options, true);
	}

	public ChromiumHeadlessDriver(ChromeDriverService service, ChromeOptions options, boolean headless) {
		super(service, headlessSettings(options, headless));
	}

	@Deprecated
	public ChromiumHeadlessDriver(Capabilities capabilities) {
		super(capabilities);
	}

	@Deprecated
	public ChromiumHeadlessDriver(ChromeDriverService service, Capabilities capabilities) {
		super(service, capabilities);
	}

	private static ChromeOptions headlessSettings(ChromeOptions options, boolean headless) {
		ChromiumFetcher fetcher = new ChromiumFetcher();
		Optional<String> binaryPath = getBinaryPathIfReady(fetcher);
		if (!binaryPath.isPresent()) {
			throw new WebDriverException("Chromium binaryPath is not ready for launching.");
		}

		options.setHeadless(headless);
		options.setBinary(binaryPath.get());
		LOG.info("Chromium binaryPath set {}", binaryPath.get());
		return options;
	}

	private static Optional<String> getBinaryPathIfReady(ChromiumFetcher fetcher) {
		Path executablePath = fetcher.getExecutablePath(CHROMIUM_BINARY_REVISION);
		if (Files.exists(executablePath)) {
			return getBinaryPath(executablePath);
		}

		try {
			download(fetcher);
		} catch (IOException e) {
			LOG.error("Download failed:", e);
			return Optional.empty();
		}
		return Files.exists(executablePath) ? getBinaryPath(executablePath) : Optional.empty();
	}

	private static Optional<String> getBinaryPath(Path executablePath) {
		return Optional.of(executablePath.toAbsolutePath().toString());
	}

	private static void download(ChromiumFetcher fetcher) throws IOException {
		if (!fetcher.canDownload(CHROMIUM_BINARY_REVISION))
			throw new WebDriverException("Cannot download chromium executable");

		LOG.info("Try downloading Chromium binary zip (revision {}).", CHROMIUM_BINARY_REVISION);
		fetcher.download(CHROMIUM_BINARY_REVISION);
	}
}
