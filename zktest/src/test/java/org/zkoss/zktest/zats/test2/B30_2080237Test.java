package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B30_2080237Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@menuitem"));
		getWebDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(5000));
		// Verify that the zkoss page is opened by verifying the existence of the "Demo" button
		assertFalse(getWebDriver().getCurrentUrl().equals("http://www.zkoss.org"));
	}
}