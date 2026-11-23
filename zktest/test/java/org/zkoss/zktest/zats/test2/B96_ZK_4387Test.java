/* B96_ZK_4387Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 07 11:42:50 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B96_ZK_4387Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:contains(append)"));
		waitResponse();
		click(jq("@chosenbox"));
		Assert.assertEquals("Append failed", "Moon", jq(".z-chosenbox-option:last").text());

		click(jq("@button:contains(prepend)"));
		waitResponse();
		click(jq("@chosenbox"));
		Assert.assertEquals("Prepend failed", "Mars", jq(".z-chosenbox-option:first").text());
	}
}
