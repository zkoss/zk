package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

/**
 * @author bob peng
 */
public class B85_ZK_3328Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$btn"));
		waitResponse();
		assertEquals("The word on the button in the alert dialog should be in Chinese.",
				"確認", jq(".z-window .z-button:eq(0)").html());
	}
}