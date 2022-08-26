/* B85_ZK_3771.java

        Purpose:
                
        Description:
                
        History:
                Mon Jan 29 5:58 PM:37 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;



public class B85_ZK_3771Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		final Label label = desktop.query("#l1").as(Label.class);
		ComponentAgent btn = desktop.query("#b1");
		btn.click();
		assertEquals("detach success", label.getValue());
	}
}
