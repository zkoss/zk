/* B90_ZK_4408Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Jan 14 16:24:31 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B90_ZK_4408Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		int bandboxWidth = jq(".z-bandbox").outerWidth();
		JQuery jqBtns = jq("@button");
		JQuery jqWindow = jq("@window");
		JQuery jqBandboxButton = jq(".z-bandbox-button");
		
		click(jqBtns.eq(0));
		waitResponse(true);
		click(jqWindow);
		waitResponse(true);
		click(jqBandboxButton);
		waitResponse();
		Assert.assertEquals("bandbox width should not change", bandboxWidth, jq(".z-bandbox").outerWidth());
		
		click(jqBtns.eq(1));
		waitResponse(true);
		click(jqWindow);
		waitResponse(true);
		click(jqBandboxButton);
		waitResponse();
		Assert.assertTrue(jq(".z-bandbox-popup").isVisible());
	}
}
