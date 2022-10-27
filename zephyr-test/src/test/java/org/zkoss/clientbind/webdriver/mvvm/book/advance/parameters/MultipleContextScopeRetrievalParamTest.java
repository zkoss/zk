/* MultipleContextScopeRetrievalParamTest.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 15:59:35 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.advance.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class MultipleContextScopeRetrievalParamTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		String host = getHost() + ":" + getPort();
		JQuery msg = jq("$msg");
		assertEquals(host, msg.text());

		click(jq("@button:eq(0)"));
		waitResponse();
		String jsessionid = msg.text();
		assertNotEquals(host, jsessionid);

		click(jq("@button:eq(1)"));
		waitResponse();
		assertEquals(host, msg.text());

		click(jq("@button:eq(2)"));
		waitResponse();
		assertEquals(jsessionid, msg.text());
	}
}
