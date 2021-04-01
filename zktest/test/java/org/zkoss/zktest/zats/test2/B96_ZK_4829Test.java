/* B96_ZK_4829Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 26 15:46:50 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.openqa.selenium.Keys;

import static org.junit.Assert.assertEquals;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Element;

/**
 * @author jameschu
 */
public class B96_ZK_4829Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Element inp = widget("@chosenbox").$n("inp");
		click(inp);
		waitResponse();
		sendKeys(inp, "b");
		waitResponse();
		sendKeys(inp, Keys.ARROW_DOWN);
		waitResponse();
		assertEquals(3, getZKLog().split("\n").length);
	}
}
