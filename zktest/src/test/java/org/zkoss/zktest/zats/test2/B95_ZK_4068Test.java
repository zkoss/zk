/* B95_ZK_4068Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Jan 18 16:33:02 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B95_ZK_4068Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		changDateboxTime(0);
		Assertions.assertEquals("1970-01-01 AM08:01", getZKLog());
		closeZKLog();
		changDateboxTime(1);
		Assertions.assertEquals("19700101AM0801", getZKLog());
		closeZKLog();
		changDateboxTime(2);
		Assertions.assertEquals("1970-0101 08PM:00", getZKLog());
		closeZKLog();
		changDateboxTime(3);
		Assertions.assertEquals("1970-01-01 AM0801", getZKLog());
		closeZKLog();
		changDateboxTime(4);
		Assertions.assertEquals("1970/01/01 08PM08:00", getZKLog());
		closeZKLog();
		changDateboxTime(5);
		Assertions.assertEquals("01197001 08PM00", getZKLog());
		closeZKLog();
		changDateboxTime(6);
		Assertions.assertEquals("19700101 AM0801", getZKLog());
		closeZKLog();
		changDateboxTime(7);
		Assertions.assertEquals("1970/1月/01 0800下午", getZKLog());
		closeZKLog();
		changDateboxTime(8);
		Assertions.assertEquals("1970-01-01 AM 08:01", getZKLog());
		closeZKLog();
		changDateboxTime(9);
		Assertions.assertEquals("Jan19700108PM200000", getZKLog());
		closeZKLog();

		type(jq("$ovd1 input"), "Jan002018");
		waitResponse();
		Assertions.assertEquals("Dec312017", getZKLog());
		closeZKLog();

		type(jq("$ovd2 input"), "Jan002018");
		waitResponse();
		Assertions.assertTrue(hasError() && !isZKLogAvailable()); // should see error and no zklog
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