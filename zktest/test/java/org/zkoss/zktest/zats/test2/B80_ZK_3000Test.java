/* B80_ZK_3000Test.java

	Purpose:
		
	Description:
		
	History:
		11:20 AM 12/22/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Element;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zktest.zats.ztl.Widget;

/**
 * @author jumperchen
 */
public class B80_ZK_3000Test extends WebDriverTestCase {
	@Test public void testZK3000() {
		connect();
		assertEquals(1, jq(".z-chosenbox-item").length());
		Widget widget = widget(jq("@chosenbox"));
		sendKeys(widget.$n("inp"), "a");
		JQuery jq = jq(".z-chosenbox-option");
		waitResponse();
		assertTrue(jq.length() > 3);

		Element element = jq.get(2);
		String text = jq(element).html();
		click(element);
		assertEquals(text, jq(jq(".z-chosenbox-item-content").get(1)).html());
	}
}
