/* B85_ZK_3653Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jul 17 17:11:45 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class B85_ZK_3653Test extends ZATSTestCase {
	@Test
	public void testVar() throws Exception {
		DesktopAgent desktop = connect();
		List<ComponentAgent> lbls = desktop.queryAll("div label");

		Assertions.assertEquals("Level 1 myvar=111 |", getLabelValueTrimmed(lbls.get(0)));
		Assertions.assertEquals("Level 2 myvar=222 |", getLabelValueTrimmed(lbls.get(1)));
		Assertions.assertEquals("Level 3 myvar=333 |", getLabelValueTrimmed(lbls.get(2)));
		Assertions.assertEquals("Level 4 myvar=444 |", getLabelValueTrimmed(lbls.get(3)));
		Assertions.assertEquals("Level 5 myvar=555 |", getLabelValueTrimmed(lbls.get(4)));
		Assertions.assertEquals("Level 6 myvar=666 |", getLabelValueTrimmed(lbls.get(5)));
		Assertions.assertEquals("Level 7 myvar=777 |", getLabelValueTrimmed(lbls.get(6)));
		Assertions.assertEquals("Level 8 myvar=888 |", getLabelValueTrimmed(lbls.get(7)));
		Assertions.assertEquals("Level 9 myvar=888 |", getLabelValueTrimmed(lbls.get(8)));
		Assertions.assertEquals("Level 10 myvar=888 |", getLabelValueTrimmed(lbls.get(9)));
	}

	private String getLabelValueTrimmed(ComponentAgent agt) {
		return agt.as(Label.class).getValue().trim();
	}

	@Test
	public void testBindVar() throws Exception {
		DesktopAgent desktop = connect();

		Assertions.assertEquals("111", getLabelValueTrimmed(desktop.query("#b1")));
		Assertions.assertEquals("222", getLabelValueTrimmed(desktop.query("#b2")));
		Assertions.assertEquals("333", getLabelValueTrimmed(desktop.query("#b3")));
		Assertions.assertEquals("444", getLabelValueTrimmed(desktop.query("#b4")));
		Assertions.assertEquals("555", getLabelValueTrimmed(desktop.query("#b5")));
		Assertions.assertEquals("666", getLabelValueTrimmed(desktop.query("#b6")));
		Assertions.assertEquals("777", getLabelValueTrimmed(desktop.query("#b7")));
		Assertions.assertEquals("888", getLabelValueTrimmed(desktop.query("#b8")));
		Assertions.assertEquals("888", getLabelValueTrimmed(desktop.query("#b9")));
		Assertions.assertEquals("888", getLabelValueTrimmed(desktop.query("#b10")));
	}
}
