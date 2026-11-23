package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

import java.util.concurrent.TimeUnit;

/**
 * @author jameschu
 */
public class B30_2080237Test extends WebDriverTestCase {
	@Test public void test() {
		connect();
		click(jq("@menuitem"));
		getWebDriver().manage().timeouts().implicitlyWait(5000, TimeUnit.SECONDS);
		// Verify that the zkoss page is opened by verifying the existence of the "Demo" button
		assertFalse(getWebDriver().getCurrentUrl().equals("http://www.zkoss.org"));
	}
}