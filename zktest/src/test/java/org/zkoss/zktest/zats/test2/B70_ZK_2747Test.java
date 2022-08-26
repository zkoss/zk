package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B70_ZK_2747Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		new WebDriverWait(driver, Duration.ofSeconds(3))
				.until(ExpectedConditions.presenceOfElementLocated(jq("#zk_showBusy")));

		assertTrue(jq("#zk_showBusy").exists());
		sleep(3000);
		assertTrue(!jq("#zk_showBusy").exists());
	}
}
