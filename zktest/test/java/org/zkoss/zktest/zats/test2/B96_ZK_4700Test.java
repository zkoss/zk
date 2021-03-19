/* B96_ZK_4700Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 18 14:43:17 CST 2021, Created by katherinelin

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B96_ZK_4700Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@chosenbox:eq(1)").find(".z-chosenbox-inplace"));
		waitResponse();
		Assert.assertTrue(jq("@chosenbox:eq(0)").find(".z-chosenbox-item").hasClass("z-chosenbox-hide"));
	}
}
