/* B86_ZK_4068Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Jan 04 16:38:23 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4068Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		changDateboxTime(0);
		Assert.assertEquals("1970-01-01 AM08:01", getZKLog());
		closeZKLog();
		changDateboxTime(1);
		Assert.assertEquals("19700101AM0801", getZKLog());
		closeZKLog();
		changDateboxTime(2);
		Assert.assertEquals("1970-0101 08PM:00", getZKLog());
		closeZKLog();
		changDateboxTime(3);
		Assert.assertEquals("1970-01-01 AM0801", getZKLog());
		closeZKLog();
		changDateboxTime(4);
		Assert.assertEquals("1970/01/01 08PM08:00", getZKLog());
		closeZKLog();
		changDateboxTime(5);
		Assert.assertEquals("01197001 8PM00", getZKLog());
		closeZKLog();
		changDateboxTime(6);
		Assert.assertEquals("19700101 AM0801", getZKLog());
		closeZKLog();
		changDateboxTime(7);
		Assert.assertEquals("1970/01/01 800PM", getZKLog());
		closeZKLog();
	}
	
	private void changDateboxTime(int index) {
		click(jq(".z-datebox-button").eq(index));
		waitResponse();
		click(jq(".z-timebox-up").eq(index));
		waitResponse();
		click(jq("$lb"));
		waitResponse();
	}
}
