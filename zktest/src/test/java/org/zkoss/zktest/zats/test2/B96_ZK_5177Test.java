/* B96_ZK_5177Test.java

	Purpose:
		
	Description:
		
	History:
		11:22 AM 2022/10/5, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.ChromiumHeadlessDriver;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5177Test extends WebDriverTestCase {
	// https://github.com/SeleniumHQ/selenium/issues/11637, noticed that there is still a bug in viewport (using --headless=new)
	protected WebDriver getWebDriver() {
		if (driver == null) {
			ChromeOptions driverOptions = getWebDriverOptions();
			// https://www.selenium.dev/blog/2023/headless-is-going-away/
			driverOptions.addArguments("--headless=new");
			driverOptions.addArguments("--disable-gpu");
			driver = new ChromiumHeadlessDriver(driverOptions, false); // intended, handled above
		}
		return driver;
	}

	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions chromeOptions = super.getWebDriverOptions();
		Map prefs = new HashMap<>();
		prefs.put("download.default_directory", System.getProperty("java.io.tmpdir"));
		prefs.put("disable-popup-blocking", "true");
		chromeOptions.setExperimentalOption("prefs", prefs);
		return chromeOptions;
	}

	@Test
	public void testDevicePixelRatio() throws IOException {
		File file = new File(System.getProperty("java.io.tmpdir"), "blob.png");
		if (file.exists()) {
			file.delete();
		}
		connect();
		click(jq("@signature").toWidget().$n("save"));
		waitResponse();
		BufferedImage read = ImageIO.read(file);

		assertEquals(300, read.getHeight(), 10);
		assertEquals(600, read.getWidth(), 10);
	}
}
