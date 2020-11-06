/* F95_ZK_555Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov 6 09:50:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.openqa.selenium.Keys;

import static org.junit.Assert.assertEquals;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */
public class F95_ZK_555Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery tb = jq("@textbox");
		click(tb);
		waitResponse();
		sendKeys(tb, Keys.TAB);
		waitResponse();
		assertEquals("Key 9 is pressed", getZKLog());
	}
}
