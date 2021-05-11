/* HeaderParamTest.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 15:10:08 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class HeaderParamTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		String host = getHost() + ":" + getPort();
		JQuery btn = jq("@button");
		Assert.assertEquals(host, jq("$msg").text());
		Assert.assertEquals("test", btn.text());

		click(btn);
		waitResponse();
		Assert.assertEquals(host, btn.text());
	}
}
