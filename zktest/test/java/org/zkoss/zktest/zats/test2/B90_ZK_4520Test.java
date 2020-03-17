/* B90_ZK_4520Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Mar 17 11:46:37 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B90_ZK_4520Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		
		JQuery textbox = jq("@textbox");
		jq("$scrollbox").scrollTop(100);
		waitResponse();
		focus(textbox);
		waitResponse();
		blur(textbox);
		waitResponse();
		
		JQuery errobox = jq("@errorbox");
		int firstPosTop = errobox.positionTop();
		click(jq("$redbox"));
		waitResponse();
		Assert.assertEquals(firstPosTop, errobox.positionTop());
	}
}
