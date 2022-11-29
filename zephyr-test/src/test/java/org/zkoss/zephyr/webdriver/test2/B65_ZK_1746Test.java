package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B65_ZK_1746Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery chosenbox = jq("@chosenbox");
		JQuery chosen_input = chosenbox.find("input");
		click(chosenbox);
		waitResponse();
		sendKeys(chosen_input, "aa");
		waitResponse();
		assertEquals(10, jq(".z-chosenbox-option").length());
		assertEquals("sublist called: \"aa\", -1\n"
				+ "getElementAt called for : 13320 -> aa0\n"
				+ "getElementAt called for : 13321 -> aa1\n"
				+ "getElementAt called for : 13322 -> aa2\n"
				+ "getElementAt called for : 13323 -> aa3\n"
				+ "getElementAt called for : 13324 -> aa4\n"
				+ "getElementAt called for : 13325 -> aa5\n"
				+ "getElementAt called for : 13326 -> aa6\n"
				+ "getElementAt called for : 13327 -> aa7\n"
				+ "getElementAt called for : 13328 -> aa8\n"
				+ "getElementAt called for : 13329 -> aa9\n"
				+ "[aa0, aa1, aa2, aa3, aa4, aa5, aa6, aa7, aa8, aa9]", getZKLog());
		closeZKLog();
		sendKeys(chosen_input, Keys.ARROW_DOWN, Keys.ARROW_DOWN, Keys.ARROW_DOWN, Keys.ENTER);
		waitResponse();
		assertTrue(jq(".z-chosenbox-item-content:contains(aa3)").exists());
		waitResponse();
		sendKeys(chosen_input, Keys.TAB);
		waitResponse();
		getActions().sendKeys(Keys.chord(Keys.SHIFT, Keys.TAB)).perform();
		waitResponse();
		closeZKLog();
		waitResponse();
		sendKeys(chosen_input, "b");
		waitResponse();
		assertEquals(10, jq(".z-chosenbox-option").length());
		assertEquals("sublist called: \"b\", -1\n"
				+ "getElementAt called for : 396 -> b0\n"
				+ "getElementAt called for : 397 -> b1\n"
				+ "getElementAt called for : 398 -> b2\n"
				+ "getElementAt called for : 399 -> b3\n"
				+ "getElementAt called for : 400 -> b4\n"
				+ "getElementAt called for : 401 -> b5\n"
				+ "getElementAt called for : 402 -> b6\n"
				+ "getElementAt called for : 403 -> b7\n"
				+ "getElementAt called for : 404 -> b8\n"
				+ "getElementAt called for : 405 -> b9\n"
				+ "[b0, b1, b2, b3, b4, b5, b6, b7, b8, b9]", getZKLog());
		assertNoAnyError();
	}
}