package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

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
}
