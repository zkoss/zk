package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Created by wenninghsu on 8/2/16.
 */
public class B80_ZK_3280Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse(true);
		Assertions.assertTrue(widget(jq("@tab").get(1)).is("selected"));
	}

}
