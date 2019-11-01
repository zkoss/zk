/* F90_ZK_4377_2Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Oct 31 15:27:26 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

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
		Assert.assertEquals("button: changed!", jq("@button:contains(button:)").eq(0).text());
		Assert.assertEquals("label: changed!", jq("@label:contains(label:)").eq(0).text());
		click(jq("@button:contains(setModelNull)"));
		waitResponse();
		checkThreeParts(0);
	}
	
	private void checkThreeParts(int expect) {
		Assert.assertEquals(expect, jq(".z-lineitem-point").length());
		Assert.assertEquals(expect, jq("@button:contains(button:)").length());
		Assert.assertEquals(expect, jq("@label:contains(label:)").length());
	}
}
