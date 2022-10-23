/* B85_ZK_3812Test.java

	Purpose:

	Description:

	History:
		Wed Jan 17 11:42:38 CST 2018, Created by jameschu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B85_ZK_3812Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		verifyResult("d1");
		click(jq("button").eq(0));
		waitResponse();
		verifyResult("d2");
		click(jq("button").eq(0));
		waitResponse();
		verifyResult("d3");
		click(jq("button").eq(0));
		waitResponse();
		verifyResult("d4");
		click(jq("button").eq(0));
		waitResponse();
		verifyResult("d5");
	}

	public void verifyResult(String result) {
		List<JQuery> labels = new ArrayList<>();
		labels.add(jq("$h1 @label"));
		labels.add(jq("$h2 @label"));
		labels.add(jq("$h3 @label"));
		labels.add(jq("$h4 @label"));
		for (int i = 0; i < labels.size(); i++) {
			Assertions.assertEquals(result, labels.get(i).text());
		}
	}
}
