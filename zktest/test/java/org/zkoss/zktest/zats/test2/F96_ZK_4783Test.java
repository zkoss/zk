/* F96_ZK_4783Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 12 14:12:20 CST 2021, Created by katherinelin

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class F96_ZK_4783Test extends WebDriverTestCase {
	@Test
	public void testIceblue() {
		connect();
		click(jq("@a:contains(Default)"));
		testMessagebox(480);
	}
	@Test
	public void testBreeze() {
		connect();
		click(jq("@a:contains(Breeze)"));
		waitResponse();
		testMessagebox(360);
	}
	@Test
	public void testSapphire() {
		connect();
		click(jq("@a:contains(Sapphire)"));
		waitResponse();
		testMessagebox(360);
	}
	@Test
	public void testSilvertail() {
		connect();
		click(jq("@a:contains(Silvertail)"));
		waitResponse();
		testMessagebox(360);
	}
	@Test
	public void testAtlantic() {
		connect();
		click(jq("@a:contains(Atlantic)"));
		waitResponse();
		testMessagebox(440);
	}
	public void testMessagebox(Integer width) {
		JQuery msgBox = jq(".z-messagebox-window");
		JQuery msgBoxViewport = jq(".z-messagebox-viewport");
		click(jq("@button:contains(alert 1)"));
		waitResponse();
		Assert.assertTrue(Boolean.valueOf(zk(msgBoxViewport).eval("hasHScroll()")));
		Assert.assertEquals(width, Integer.valueOf(msgBox.outerWidth()));
		click(msgBox.find("@button"));

		click(jq("@button:contains(messagebox1)"));
		waitResponse();
		Assert.assertFalse(Boolean.valueOf(zk(msgBoxViewport).eval("hasHScroll()")));
		Assert.assertEquals(width, Integer.valueOf(msgBox.outerWidth()));
		click(msgBox.find("@button"));

		click(jq("@button:contains(alert 2)"));
		waitResponse();
		Assert.assertFalse(Boolean.valueOf(zk(msgBoxViewport).eval("hasHScroll()")));
		Assert.assertEquals(width, Integer.valueOf(msgBox.outerWidth()));
		click(msgBox.find("@button"));

		click(jq("@button:contains(messagebox2)"));
		waitResponse();
		Assert.assertFalse(Boolean.valueOf(zk(msgBoxViewport).eval("hasHScroll()")));
		Assert.assertEquals(600, msgBox.outerWidth());
		click(msgBox.find("@button"));
	}
}
