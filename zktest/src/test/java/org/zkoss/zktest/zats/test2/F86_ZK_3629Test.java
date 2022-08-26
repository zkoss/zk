/* F86_ZK_3629Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Oct 17 14:54:10 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F86_ZK_3629Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		String indeterminateClass = "z-progressmeter-indeterminate";
		JQuery pm2 = jq("$pm2");
		JQuery isbtn = jq("$isIndeterminate");
		JQuery lb = jq("$lb");
		Assertions.assertTrue(jq("$pm1").hasClass(indeterminateClass));
		Assertions.assertFalse(pm2.hasClass(indeterminateClass));
		
		click(isbtn);
		waitResponse();
		Assertions.assertEquals("false", getZKLog());
		
		click(jq("$setTrue"));
		waitResponse();
		Assertions.assertTrue(pm2.hasClass(indeterminateClass));
		
		click(isbtn);
		waitResponse();
		Assertions.assertEquals("false\ntrue", getZKLog());
		
		click(jq("$setValue75"));
		waitResponse();
		Assertions.assertEquals("75", lb.html());
		
		click(jq("$setValue25"));
		waitResponse();
		Assertions.assertEquals("25", lb.html());
		
		click(jq("$setFalse"));
		waitResponse();
		Assertions.assertFalse(pm2.hasClass(indeterminateClass));
		Assertions.assertEquals(50, pm2.children(".z-progressmeter-image").width(), 1);
		
		click(isbtn);
		waitResponse();
		Assertions.assertEquals("false\ntrue\nfalse", getZKLog());
	}
}
