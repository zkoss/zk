package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import net.jcip.annotations.NotThreadSafe;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

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
			assertEquals("The word on the button in the alert dialog should be in Chinese.",
					"確認", jq(".z-window .z-button:eq(0)").html());
		} finally {
			click(jq("@button:last"));
			waitResponse();
		}
	}
}
