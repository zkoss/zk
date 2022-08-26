/* B96_ZK_4800Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 12 15:03:42 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class B96_ZK_4845Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		desktopAgent.query("button").click();
		assertEquals("Second", desktopAgent.query("#l1").as(Label.class).getValue());
		assertEquals("Second", desktopAgent.query("#l2").as(Label.class).getValue());
	}
}
