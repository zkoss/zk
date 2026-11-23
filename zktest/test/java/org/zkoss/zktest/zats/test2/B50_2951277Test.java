/* B50_2951277Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 21 14:58:00 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2951277Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assert.assertEquals("Just a \"test\" with quotes", jq("@textbox").val());
	}
}
