/* B96_ZK_4840Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Apr 08 15:21:19 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B96_ZK_4840Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		checkTextAlignment("nav");
		click(jq("@nav:eq(0)"));
		waitResponse();
		checkTextAlignment("navitem");
	}

	private void checkTextAlignment(String targetName) {
		int textOffsetLeft = -1;
		for (int i = 0; i < 5; i++) {
			int target = jq(".z-" + targetName + "-text").eq(i).offsetLeft();
			if (textOffsetLeft == -1)
				textOffsetLeft = target;
			else
				Assert.assertEquals("you should see all the " + targetName + " with consistent text alignment", textOffsetLeft, target, 1);
		}
	}
}
