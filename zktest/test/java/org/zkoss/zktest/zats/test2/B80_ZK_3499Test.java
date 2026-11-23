package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * Created by wenninghsu on 12/19/16.
 */
public class B80_ZK_3499Test extends WebDriverTestCase{

	@Test
	public void test () {
		connect();
		click(jq("@menu:eq(0) > a"));
		waitResponse(true);
		assertEquals("true", widget(jq("@label:eq(1)")).get("value"));
		click(jq("@menu:eq(0) > a"));
		waitResponse(true);
		click(jq("@menu:eq(3) > a"));
		waitResponse(true);
		assertEquals("true", widget(jq("@label:eq(2)")).get("value"));
		click(jq("@menu:eq(3) > a"));
		waitResponse(true);

	}


}
