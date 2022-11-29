package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F80_ZK_3133Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		assertTrue(jq("@iframe").contents().find("body").text().contains("org.zkoss.zk.ui.UiException: there are more then one MatchMedia method \"all and (min-width: 501px)\" in class"));

		assertEquals("onCreate", getZKLog());

		JQuery l1 = jq("@window").eq(0).find("@label");
		JQuery l2 = jq("@window").eq(1).find("@label");

		assertEquals("browser width > 500px", l1.text().trim());
		assertEquals("browser width > 500px", l2.text().trim());

		final Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(450, size.height));
		waitResponse();
		assertEquals("browser width = 450px", l1.text().trim());
		assertEquals("browser width = 450px", l2.text().trim());

		refreshPage();
		waitResponse();
		assertEquals("browser width = 450px", l1.text().trim());
		assertEquals("browser width = 450px", l2.text().trim());

		driver.manage().window().setSize(new Dimension(550, size.height));
		waitResponse();
		assertEquals("browser width > 500px", l1.text().trim());
		assertEquals("browser width > 500px", l2.text().trim());

	}

	private void refreshPage() {
		driver.navigate().refresh();
		new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.className("z-page")));
	}
}
