/* B110_ZK_6192_TabletTest.java

        Purpose:
                
        Description:
                
        History:
                Tue Oct 20 09:47:23 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.PointerInput;

import org.zkoss.zktest.zats.TabletWebDriverTestCase;

public class B110_ZK_6192_TabletTest extends TabletWebDriverTestCase {
	@Test
	public void tapRepositionsTooltip() {
		connect("/test2/B110-ZK-6192.zul");
		waitResponse();
		assertEquals("true", getEval("'' + !!zk.mobile"), "zk.mobile must be on");
		getActions().setActivePointer(PointerInput.Kind.TOUCH, "finger")
				.moveToElement(toElement(jq("$edgeRange .z-sliderbuttons-button").eq(1))).click().perform();
		waitResponse();
		assertEquals("true", getEval(B110_ZK_6192Test.buttonScript("edgeRange", 1,
				"t.width > 0 && t.right <= r.left && t.left >= 0")),
				"the tapped tooltip should appear on the left, inside the viewport");
	}
}
