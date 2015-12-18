/* B80_ZK_3017Test.java

	Purpose:
		
	Description:
		
	History:
		2:23 PM 12/17/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zktest.zats.ztl.Widget;

/**
 * @author jumperchen
 */
public class B80_ZK_3017Test extends WebDriverTestCase {

	@Test
	public void test() throws IOException {
		connect();
		Widget textbox1 = widget("$textbox1");
		Widget textbox2 = widget("$textbox2");
		testRegex(textbox1);
		testRegex(textbox2);
	}

	private void testRegex(Widget widget) {
		type(widget, "123456");
		waitResponse();
		JQuery jq = jq(".z-errorbox-close");
		assertTrue(jq.exists());
		assertEquals("myMsg", jq(".z-errorbox-content").html());
		type(widget, "123,45");
		waitResponse();
		assertFalse(jq.exists());
	}
}
