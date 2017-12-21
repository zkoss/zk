/* B85_ZK_3657Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 21 18:31:08 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B85_ZK_3657Test extends WebDriverTestCase {
	@Test
	public void testFlexMinWidth() {
		connect();

		String baseWidth = jq("$outer1").css("width");
		Assert.assertEquals("The width of #outer2 is wrong!",
				baseWidth, jq("$outer2").css("width"));
		Assert.assertEquals("The width of #outer3 is wrong!",
				baseWidth, jq("$outer3").css("width"));
	}

	@Test
	public void testFlexMinHeight() {
		connect();

		String baseHeight = jq("$outer1").css("height");
		Assert.assertEquals("The height of #outer2 is wrong!",
				baseHeight, jq("$outer2").css("height"));
		Assert.assertEquals("The height of #outer3 is wrong!",
				baseHeight, jq("$outer3").css("height"));
	}
}
