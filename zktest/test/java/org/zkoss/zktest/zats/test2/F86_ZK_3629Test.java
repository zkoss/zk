/* F86_ZK_3629Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Oct 17 14:54:10 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class F86_ZK_3629Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		String indeterminateClass = "z-progressmeter-indeterminate";
		JQuery pm2 = jq("$pm2");
		JQuery isbtn = jq("$isIndeterminate");
		JQuery lb = jq("$lb");
		Assert.assertTrue(jq("$pm1").hasClass(indeterminateClass));
		Assert.assertFalse(pm2.hasClass(indeterminateClass));
		
		click(isbtn);
		waitResponse();
		Assert.assertEquals("false", getZKLog());
		
		click(jq("$setTrue"));
		waitResponse();
		Assert.assertTrue(pm2.hasClass(indeterminateClass));
		
		click(isbtn);
		waitResponse();
		Assert.assertEquals("false\ntrue", getZKLog());
		
		click(jq("$setValue75"));
		waitResponse();
		Assert.assertEquals("75", lb.html());
		
		click(jq("$setValue25"));
		waitResponse();
		Assert.assertEquals("25", lb.html());
		
		click(jq("$setFalse"));
		waitResponse();
		Assert.assertFalse(pm2.hasClass(indeterminateClass));
		Assert.assertEquals(50, pm2.children(".z-progressmeter-image").width());
		
		click(isbtn);
		waitResponse();
		Assert.assertEquals("false\ntrue\nfalse", getZKLog());
	}
}
