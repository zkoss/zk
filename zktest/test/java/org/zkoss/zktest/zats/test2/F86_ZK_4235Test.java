/* F86_ZK_4235Test.java

		Purpose:
		
		Description:
		
		History:
				Fri May 10 17:09:02 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class F86_ZK_4235Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		checkYear("1930", "2029");
		click(jq("@button").eq(0));
		waitResponse();
		checkYear("30", "129");
		click(jq("@button").eq(1));
		waitResponse();
		checkYear("530", "529");
		click(jq("@button").eq(2));
		waitResponse();
		checkYear("1530", "1529");
		click(jq("@button").eq(3));
		waitResponse();
		checkYear("2030", "2029");
		click(jq("@button").eq(4));
		waitResponse();
		checkYear("200030", "200029");
	}
	
	private void checkYear(String expect1, String expect2) {
		click(jq(".z-datebox-button").eq(0));
		waitResponse();
		Assert.assertEquals(expect1, jq(".z-calendar-text:last").text().trim());
		click(jq(".z-datebox-button").eq(1));
		waitResponse();
		Assert.assertEquals(expect2, jq(".z-calendar-text:last").text().trim());
	}
}
