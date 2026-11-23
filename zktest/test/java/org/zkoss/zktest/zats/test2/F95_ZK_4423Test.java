/* F95_ZK_4423Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Nov 03 16:48:48 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Widget;

public class F95_ZK_4423Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Widget portalchildren1 = jq(".z-portalchildren").eq(0).toWidget();
		Widget portalchildren2 = jq(".z-portalchildren").eq(1).toWidget();
		Widget portalchildren3 = jq(".z-portalchildren").eq(2).toWidget();

		Assert.assertEquals(2, portalchildren1.nChildren());
		Assert.assertEquals(2, portalchildren2.nChildren());
		Assert.assertEquals(2, portalchildren3.nChildren());

		dragAndDrop(jq(".z-panel-header-move").eq(0), jq(".z-panel-header-move").eq(4));
		waitResponse();
		Assert.assertEquals("cross 2 column!", getZKLog()); // should not move since VM logic
		closeZKLog();
		Assert.assertEquals(2, portalchildren1.nChildren());
		Assert.assertEquals(2, portalchildren2.nChildren());
		Assert.assertEquals(2, portalchildren3.nChildren());

		dragAndDrop(jq(".z-panel-header-move").eq(5), jq(".z-panel-header-move").eq(3));
		waitResponse();
		Assert.assertEquals("{todo=[todo task 1, todo task 2], active=[active task 1, complete task 2, active task 2], complete=[complete task 1]}", getZKLog());
		closeZKLog();
		Assert.assertEquals(2, portalchildren1.nChildren());
		Assert.assertEquals(3, portalchildren2.nChildren());
		Assert.assertEquals(1, portalchildren3.nChildren());

		dragAndDrop(jq(".z-panel-header-move").eq(3), jq(".z-panel-header-move").eq(0));
		waitResponse();
		Assert.assertEquals("{todo=[complete task 2, todo task 1, todo task 2], active=[active task 1, active task 2], complete=[complete task 1]}", getZKLog());
		closeZKLog();
		Assert.assertEquals(3, portalchildren1.nChildren());
		Assert.assertEquals(2, portalchildren2.nChildren());
		Assert.assertEquals(1, portalchildren3.nChildren());

		dragAndDrop(jq(".z-panel-header-move").eq(1), jq(".z-panel-header-move").eq(5));
		waitResponse();
		Assert.assertEquals("cross 2 column!", getZKLog()); // should not move since VM logic
		closeZKLog();
		Assert.assertEquals(3, portalchildren1.nChildren());
		Assert.assertEquals(2, portalchildren2.nChildren());
		Assert.assertEquals(1, portalchildren3.nChildren());

		dragAndDrop(jq(".z-panel-header-move").eq(0), jq(".z-panel-header-move").eq(1));
		waitResponse();
		Assert.assertEquals("{todo=[todo task 1, complete task 2, todo task 2], active=[active task 1, active task 2], complete=[complete task 1]}", getZKLog());
		closeZKLog();
		Assert.assertEquals(3, portalchildren1.nChildren());
		Assert.assertEquals(2, portalchildren2.nChildren());
		Assert.assertEquals(1, portalchildren3.nChildren());
	}
}
