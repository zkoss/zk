/* B86_ZK_4269Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 30 10:38:06 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B86_ZK_4269Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery rect = jq("@cropper").find(".z-cropper-holder > div > div");
		Assert.assertTrue("The rectangle should be visible", rect.isVisible());
		Assert.assertEquals(250, rect.width());
		Assert.assertEquals(250, rect.height());
	}
}
