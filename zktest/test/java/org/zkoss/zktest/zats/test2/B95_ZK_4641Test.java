/* B95_ZK_4641Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Dec 03 12:53:31 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B95_ZK_4641Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery btnsFromA = jq("$groupA > @button");
		JQuery btnsFromB = jq("$groupB > @button");
		JQuery btnsFromC = jq("$groupC > @button");

		// 1-A. test set constraint Constraint not between before or after
		clickTest(btnsFromA.eq(0), "before end");
		clickTest(btnsFromA.eq(1), "between begin and end");
		clickTest(btnsFromA.eq(2), "after begin");
		clickTest(btnsFromA.eq(3), "between begin and end");

		// 1-B. test set constraint Constraint between before or after
		for (int i = 0; i < 4; i++) {
			clickTest(btnsFromA.eq(0), "before end");
			clickTest(btnsFromB.eq(i), "begin and end is clear");
		}

		// 1-C. test set constraint empty
		clickTest(btnsFromA.eq(2), "after begin");
		clickTest(btnsFromC.eq(0), "begin and end is clear");
		// 1-C. test set constraint null
		clickTest(btnsFromA.eq(1), "between begin and end");
		clickTest(btnsFromC.eq(1), "begin and end is clear");
	}

	private void clickTest(JQuery button, String expectedLog) {
		click(button);
		waitResponse();
		click(jq("$test"));
		waitResponse();
		Assert.assertEquals("client side calendar begin and end should be updated/removed", expectedLog, getZKLog());
		closeZKLog();
	}
}
