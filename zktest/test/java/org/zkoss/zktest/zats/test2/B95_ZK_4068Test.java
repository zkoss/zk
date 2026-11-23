/* B95_ZK_4068Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Jan 18 16:33:02 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B95_ZK_4068Test extends WebDriverTestCase {
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
		Assert.assertEquals("01197001 08PM00", getZKLog());
		closeZKLog();
		changDateboxTime(6);
		Assert.assertEquals("19700101 AM0801", getZKLog());
		closeZKLog();
		changDateboxTime(7);
		Assert.assertEquals("1970/1月/01 0800下午", getZKLog());
		closeZKLog();
		changDateboxTime(8);
		Assert.assertEquals("1970-01-01 AM 08:01", getZKLog());
		closeZKLog();
		changDateboxTime(9);
		Assert.assertEquals("Jan19700108PM200000", getZKLog());
		closeZKLog();

		type(jq("$ovd1 input"), "Jan002018");
		waitResponse();
		Assert.assertEquals("Dec312017", getZKLog());
		closeZKLog();

		type(jq("$ovd2 input"), "Jan002018");
		waitResponse();
		Assert.assertTrue(hasError() && !isZKLogAvailable()); // should see error and no zklog
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