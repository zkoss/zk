/* TabletWebDriverTestCase.java

	Purpose:

	Description:

	History:
		Thu Aug 20 11:05:41 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats;

import java.util.Map;

import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A base class running the browser with a tablet user agent and touch support,
 * so that <code>zk.mobile</code>, <code>zk.touchEnabled</code> and
 * <code>zk.tabletUIEnabled</code> are all on and the zkmax tablet molds apply.
 *
 * <p>A subclass must be annotated with
 * {@link org.zkoss.test.webdriver.ForkJVMTestOnly}: the tablet UI is turned on
 * by a library property, which is JVM wide.
 *
 * @author peakerlee
 */
public abstract class TabletWebDriverTestCase extends WebDriverTestCase {
	/** An Android tablet user agent, so that <code>zk.mobile</code> is true. */
	private static final String TABLET_USER_AGENT = "Mozilla/5.0 (Linux; Android 13; SM-X710)"
			+ " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

	/** zktest disables the tablet UI by default, turn it back on. */
	@RegisterExtension
	public static final ExternalZkXml TABLET_UI = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Map.of(
						"userAgent", TABLET_USER_AGENT,
						"deviceMetrics", Map.of("width", 1024, "height", 768,
								"pixelRatio", 2, "touch", true)));
	}
}
