/* B96_ZK_4282Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul 08 10:14:07 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B96_ZK_4282Test extends WebDriverTestCase {
	@Test
	public void testSingle() {
		connect();

		click(jq("$lb1-1 @listitem:eq(0)"));
		waitResponse();
		Assert.assertTrue(jq("$lb1-2 @listitem:eq(0)").hasClass("z-listitem-selected"));
	}

	@Test
	public void testMultiple() {
		connect();

		click(jq("$lb2-1 @listitem:eq(0)"));
		waitResponse();
		Assert.assertTrue(jq("$lb2-2 @listitem:eq(0)").hasClass("z-listitem-selected"));
	}
}
