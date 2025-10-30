package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author bob peng
 */
@NotThreadSafe
public class B85_ZK_3328Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		try {
			click(jq("$btn"));
			waitResponse();
			assertEquals("確認", jq(".z-window .z-button:eq(0)").html(), "The word on the button in the alert dialog should be in Chinese.");
		} finally {
			click(jq("@button:last"));
			waitResponse();
		}
	}
}
