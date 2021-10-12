/* B96_ZK_4862Test.java.

	Purpose:
		
	Description:
		
	History:
		Thu May 28 12:50:22 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */
public class B96_ZK_4862Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		click(jq("@textbox"));
		waitResponse();
		JQuery $windowContent = jq(".z-window-content");
		$windowContent.scrollTop($windowContent.scrollHeight());
		waitResponse();
		blur(jq("@textbox"));
		waitResponse();
		$windowContent.scrollTop(0);
		waitResponse();
		assertTrue(jq(".z-errorbox").isVisible());
	}
}
