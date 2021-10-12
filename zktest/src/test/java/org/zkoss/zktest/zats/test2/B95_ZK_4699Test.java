/* B95_ZK_4699Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Dec 04 14:31:38 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B95_ZK_4699Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery listitems = jq("@listitem");
		JQuery textboxs = jq("@textbox");

		click(listitems.eq(0));
		waitResponse();
		Assert.assertTrue("first listitem should have focus style", listitems.eq(0).hasClass("z-listitem-focus"));
		click(textboxs.eq(3));
		waitResponse();
		Assert.assertFalse("first listitem should not have focus style", listitems.eq(0).hasClass("z-listitem-focus"));
		Assert.assertTrue("4th textbox should be focused", textboxs.eq(3).is(":focus"));
	}
}
