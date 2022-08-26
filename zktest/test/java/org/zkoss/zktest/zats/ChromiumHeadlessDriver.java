/* ChromiumHeadlessDriver.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 07 16:16:29 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Capabilities;
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
	static {
		WebDriverManager.chromiumdriver().setup();
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
		options.setHeadless(headless);
		return options;
	}
}
