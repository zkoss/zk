/* B85_ZK_3838Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Jun 12 16:06:35 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.ClientWidget;

public class B85_ZK_3838Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		copyPastToIntboxTest(jq("@textbox").eq(0), "");
		copyPastToIntboxTest(jq("@textbox").eq(1), "123");
	}

	private void copyPastToIntboxTest(ClientWidget copyTarget, String expect) {
		click(copyTarget);
		waitResponse();
		selectAll();
		copy();
		waitResponse();
		click(jq("@intbox"));
		waitResponse();
		paste();
		waitResponse();
		blur(jq("@intbox"));
		Assert.assertEquals(expect, jq("@intbox").val());
	}
}
