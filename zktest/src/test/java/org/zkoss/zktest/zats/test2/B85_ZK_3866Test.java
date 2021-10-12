package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

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
			assertEquals("Error: selection not match", Integer.toString(i), sel);
		}
	}
}