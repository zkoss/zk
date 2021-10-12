/* B96_ZK_4916Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Jun 11 10:07:18 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B96_ZK_4916Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:contains(foo)"));
		waitResponse();
		Assert.assertEquals("foo", jq(".z-tbeditor-editor").html());

		click(jq("@button:contains(bar)"));
		waitResponse();
		Assert.assertEquals("bar", jq(".z-tbeditor-editor").html());
	}
}
