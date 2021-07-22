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
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.HasTouchScreen;
import org.openqa.selenium.interactions.TouchScreen;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteTouchScreen;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A local running Chromium ChromeDriver. By default it runs in headless mode.
 *
 * @author rudyhuang
 */
public class ChromiumHeadlessDriver extends RemoteWebDriver implements HasTouchScreen {
	private static final Logger LOG = LoggerFactory.getLogger(ChromiumHeadlessDriver.class);
	private static final String CHROME_DRIVER_VERSION = "91.0.4472.19";
	private static final int CHROMIUM_BINARY_REVISION = 869685; // from puppeteer v9.0.0 Chromium 91.0.4469.0 (r869685)

	static {
		WebDriverManager.chromiumdriver().driverVersion(CHROME_DRIVER_VERSION).setup();
		System.setProperty("webdriver.chrome.logfile", String.format("%s/chromedriver.log", System.getProperty("java.io.tmpdir")));
		System.setProperty("webdriver.chrome.verboseLogging", "true");
	}

	private final TouchScreen touchScreen;

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
		this(headlessSettings(options, headless));
	}

	@Deprecated
	public ChromiumHeadlessDriver(Capabilities capabilities) {
		super(new HttpCommandExecutor(ChromiumHeadlessDriver.getChromeDriverServiceInstance().getUrl()), capabilities);
		touchScreen = new RemoteTouchScreen(getExecuteMethod());
	}

	private static Capabilities headlessSettings(ChromeOptions options, boolean headless) {
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

	@Override
	public TouchScreen getTouch() {
		return touchScreen;
	}

	public static ChromeDriverService getChromeDriverServiceInstance() {
		final ChromeDriverService service = Holder.SERVICE;
		if (!service.isRunning()) {
			try {
				service.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return service;
	}

	private static class Holder {
		static final ChromeDriverService SERVICE = ChromeDriverService.createDefaultService();
		static {
			Runtime.getRuntime().addShutdownHook(new Thread(SERVICE::stop));
		}
	}
}
