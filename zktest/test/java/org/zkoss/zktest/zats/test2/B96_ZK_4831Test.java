/* B96_ZK_4831Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Apr 09 11:53:56 CST 2021, Created by katherinelin

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B96_ZK_4831Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		String background = jq("$div").css("background-image");
		Assert.assertEquals("url(\"http://localhost:8080/zktest/test2/img/icon_browser.png\")", background);
	}
}
