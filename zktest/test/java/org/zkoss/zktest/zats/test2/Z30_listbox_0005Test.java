/* Z30_listbox_0005Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 29 10:54:37 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class Z30_listbox_0005Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery buttons = jq(".z-button");
		verify(buttons.eq(0), "true");
		verify(buttons.eq(1), "false");
	}

	private void verify(JQuery button, String log) {
		click(button);
		waitResponse();
		Assert.assertEquals(log, getZKLog());
		closeZKLog();
	}
}
