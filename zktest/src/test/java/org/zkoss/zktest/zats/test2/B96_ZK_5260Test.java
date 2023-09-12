package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B96_ZK_5260Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		waitResponse();

		final String textContent = "4@email.io <aerror";

		click(jq("@chosenbox").find("input"));
		waitResponse();
		final JQuery chosenOption = jq(".z-chosenbox-option:eq(3)");
		assertEquals(textContent, chosenOption.text());

		// `click(chosenOption);` is flaky. Selenium might fail with "Other element would receive the click".
		// Thus, I click the element by evaluating the following JS snippet.
		eval("jq('.z-chosenbox-option:eq(3)')[0].click()"); // Equivalent to `click(chosenOption);`.
		waitResponse();
		assertEquals(textContent, jq(".z-chosenbox-item-content").text());
	}

	@Test
	public void testAllowScripts() {
		Actions act = new Actions(connect());
		click(jq("@chosenbox").find("input"));
		waitResponse();
		act.moveToElement(toElement(jq(".z-chosenbox-option:eq(5)").children())).perform();
		assertFalse(showAlert());
		act.moveToElement(toElement(jq(".z-chosenbox-option:eq(6)").children())).perform();
		assertFalse(showAlert());

		act.sendKeys(Keys.ESCAPE).perform();
		waitResponse();
		click(jq("@button"));
		waitResponse();
		click(jq("@chosenbox").find("input"));
		waitResponse();
		act.moveToElement(toElement(jq(".z-chosenbox-option:eq(5)").children())).perform();
		assertTrue(showAlert());
		act.moveToElement(toElement(jq(".z-chosenbox-option:eq(6)").children())).perform();
		assertTrue(showAlert());
	}

	private boolean showAlert() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
			wait.until(ExpectedConditions.alertIsPresent());
			Alert alert = driver.switchTo().alert();
			alert.accept();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
