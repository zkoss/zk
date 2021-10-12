/* Z35_tabbox_202Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Jun 19 16:44:20 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class Z35_tabbox_202Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		int tabboxleftWidth = jq(".z-tabbox-left").width();
		click(jq("@tab:contains(Tab 2)"));
		waitResponse();
		click(jq("@tab:contains(Tab 1)"));
		waitResponse();
		Assert.assertEquals(tabboxleftWidth, jq(".z-tabbox-left").width());
	}
}
