/* F90_ZK_4377_2Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Oct 31 15:27:26 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F90_ZK_4377_2Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		checkThreeParts(4);
		click(jq("@button:contains(add item)"));
		waitResponse();
		checkThreeParts(5);
		click(jq("@button:contains(remove item)"));
		waitResponse();
		checkThreeParts(4);
		click(jq("@button:contains(setModelABC)"));
		waitResponse();
		checkThreeParts(3);
		click(jq("@button:contains(change item)"));
		waitResponse();
		Assertions.assertEquals("button: changed!", jq("@button:contains(button:)").eq(0).text());
		Assertions.assertEquals("label: changed!", jq("@label:contains(label:)").eq(0).text());
		click(jq("@button:contains(setModelNull)"));
		waitResponse();
		checkThreeParts(0);
	}
	
	private void checkThreeParts(int expect) {
		Assertions.assertEquals(expect, jq(".z-lineitem-point").length());
		Assertions.assertEquals(expect, jq("@button:contains(button:)").length());
		Assertions.assertEquals(expect, jq("@label:contains(label:)").length());
	}
}
