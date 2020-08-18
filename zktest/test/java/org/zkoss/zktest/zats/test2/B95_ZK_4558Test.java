/* B95_ZK_4558Test.java

	Purpose:
		
	Description:
		
	History:
		Fri, Aug 14, 2020  02:48:18 PM, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */
public class B95_ZK_4558Test extends WebDriverTestCase {
	private static final int DRAG_THRESHOLD = 2;
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		JQuery h2 = jq("$header2");
		int h2Width = h2.outerWidth();
		getActions().moveToElement(toElement(h2), h2Width - DRAG_THRESHOLD, 15)
				.clickAndHold()
				.moveByOffset(-20, 0)
				.release()
				.perform();
		waitResponse();
		click(jq("@button"));
		waitResponse();
		Assert.assertTrue(jq("$header1").width() >= 1);
	}
}
