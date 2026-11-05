package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
@ForkJVMTestOnly
public class B30_1753712Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml(B30_1753712Test.class);

	@Test
	public void test() {
		connect();
		assertEquals("n/a", jq("$la").text());
		String url = driver.getCurrentUrl();
		// a refresh's own GET can reach the server before the rmDesktop beacon; leave the page first, then reload
		driver.get("about:blank");
		sleep(1500);
		driver.get(url);
		waitResponse();
		assertNotEquals("n/a", jq("$la").text());
	}
}
