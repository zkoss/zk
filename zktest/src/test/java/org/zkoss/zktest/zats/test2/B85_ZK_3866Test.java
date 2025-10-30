package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;



/**
 * @author bob peng
 */
public class B85_ZK_3866Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		// click the button and open the popup
		click(jq("$btn"));
		waitResponse(true);
		System.out.println("original selection: " + jq("input[type=radio]:checked + label").html());

		for (int i = 1; i <= 2; i++) {
			// select radio (index: i)
			click(jq("input[type=radio]:eq(" + i + ")"));
			waitResponse(true);
			// check the selected radio
			String sel = jq("input[type=radio]:checked + label").html();
			System.out.println("current selection: " + sel);
			assertEquals(Integer.toString(i), sel, "Error: selection not match");
		}
	}
}