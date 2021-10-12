/* B50_2959024Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 21 16:32:51 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.containsString;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2959024Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();

		Assert.assertTrue(jq(".z-messagebox-window").isVisible());
		Assert.assertThat(jq(".z-messagebox").text(), containsString("If you can see the message, the bug is fixed!"));
	}
}
