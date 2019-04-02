/* B50_ZK_244Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Apr 03 11:45:21 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B50_ZK_244Test extends WebDriverTestCase {
	@Test
	public void test(){
		connect();
		JQuery body = jq("body");
		JQuery zwindow = jq(".z-window");
		click(jq(".z-button"));
		waitResponse();
		click(jq(".z-window-maximize"));
		waitResponse();
		Assert.assertEquals(body.outerWidth(), zwindow.outerWidth());
		Assert.assertEquals(body.outerHeight(), zwindow.outerHeight());
	}
}
