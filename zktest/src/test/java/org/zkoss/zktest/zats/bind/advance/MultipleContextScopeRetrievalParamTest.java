/* MultipleContextScopeRetrievalParamTest.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 15:59:35 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class MultipleContextScopeRetrievalParamTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		String host = getHost() + ":" + getPort();
		JQuery msg = jq("$msg");
		Assert.assertEquals(host, msg.text());

		click(jq("@button:eq(0)"));
		waitResponse();
		String jsessionid = msg.text();
		Assert.assertNotEquals(host, jsessionid);

		click(jq("@button:eq(1)"));
		waitResponse();
		Assert.assertEquals(host, msg.text());

		click(jq("@button:eq(2)"));
		waitResponse();
		Assert.assertEquals(jsessionid, msg.text());
	}
}
