/* B95_ZK_4729Test.java

		Purpose:

		Description:

		History:
				Thu Dec 10 11:10:37 CST 2020, Created by katherinelin

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B95_ZK_4729Test extends WebDriverTestCase {
	@Test
	public void test() throws InterruptedException {
		connect();

		JQuery textbox = jq("@textbox");
		JQuery errobox = jq("@errorbox");
		focus(textbox);
		blur(textbox);
		waitResponse();
		jq("$scrollbox").scrollTop(1000);
		waitResponse();
		click(jq("$pinkbox"));
		waitResponse();
		Assert.assertTrue(errobox.offsetTop() > 0);
	}
}
