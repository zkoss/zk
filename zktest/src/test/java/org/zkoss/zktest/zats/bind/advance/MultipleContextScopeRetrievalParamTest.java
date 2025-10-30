/* MultipleContextScopeRetrievalParamTest.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 15:59:35 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class MultipleContextScopeRetrievalParamTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		String host = getHost() + ":" + getPort();
		JQuery msg = jq("$msg");
		Assertions.assertEquals(host, msg.text());

		click(jq("@button:eq(0)"));
		waitResponse();
		String jsessionid = msg.text();
		Assertions.assertNotEquals(host, jsessionid);

		click(jq("@button:eq(1)"));
		waitResponse();
		Assertions.assertEquals(host, msg.text());

		click(jq("@button:eq(2)"));
		waitResponse();
		Assertions.assertEquals(jsessionid, msg.text());
	}
}
