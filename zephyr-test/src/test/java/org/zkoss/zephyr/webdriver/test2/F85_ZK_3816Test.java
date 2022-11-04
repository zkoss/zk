package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class F85_ZK_3816Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		sleep(2000);
		clickBtns();
		Assertions.assertEquals("+*-$", jq("$win #result").text());
		Assertions.assertEquals("+*-$ 123", jq("$win #str").text());
		click(jq("button").eq(4));
		waitResponse();
		Assertions.assertTrue(jq("$win #msg").text().contains("done"));
		clickBtns();
		Assertions.assertEquals("+*-$+*-$", jq("$win #result").text());
		Assertions.assertEquals("+*-$+*-$ 123", jq("$win #str").text());
	}

	private void clickBtns() {
		JQuery btns = jq("@button");
		for (int i = 0; i < 4; i++) {
			click(btns.eq(i));
			waitResponse();
		}
	}
}
