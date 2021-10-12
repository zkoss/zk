/* Z30_listbox_0006Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 29 11:00:39 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class Z30_listbox_0006Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		jq(".z-button").forEach(button -> {
			click(button);
			waitResponse();
			Assert.assertEquals("true", getZKLog());
			closeZKLog();
		});
	}
}
