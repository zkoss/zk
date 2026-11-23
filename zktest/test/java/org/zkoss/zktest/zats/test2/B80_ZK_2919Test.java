/* B80_ZK_2919Test.java

	Purpose:
		
	Description:
		
	History:
		11:35 AM 12/22/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jumperchen
 */
public class B80_ZK_2919Test extends WebDriverTestCase {
	@Test
	public void testZK2919() {
		connect();
		JQuery buttons = jq("@button");
		click(buttons.get(0));
		waitResponse();
		assertEquals("Not valid", jq(".z-errorbox-content").html());

		selectComboitem(jq("@combobox").toWidget(), 0);

		assertFalse(jq(".z-errorbox-content").exists());
		click(buttons.get(1));
		waitResponse();
		assertEquals("value 1", jq(".z-messagebox").find(".z-label").html());
	}
}
