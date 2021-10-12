/* B85_ZK_3653Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jul 17 17:11:45 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

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

		Assert.assertEquals("Level 1 myvar=111 |", getLabelValueTrimmed(lbls.get(0)));
		Assert.assertEquals("Level 2 myvar=222 |", getLabelValueTrimmed(lbls.get(1)));
		Assert.assertEquals("Level 3 myvar=333 |", getLabelValueTrimmed(lbls.get(2)));
		Assert.assertEquals("Level 4 myvar=444 |", getLabelValueTrimmed(lbls.get(3)));
		Assert.assertEquals("Level 5 myvar=555 |", getLabelValueTrimmed(lbls.get(4)));
		Assert.assertEquals("Level 6 myvar=666 |", getLabelValueTrimmed(lbls.get(5)));
		Assert.assertEquals("Level 7 myvar=777 |", getLabelValueTrimmed(lbls.get(6)));
		Assert.assertEquals("Level 8 myvar=888 |", getLabelValueTrimmed(lbls.get(7)));
		Assert.assertEquals("Level 9 myvar=888 |", getLabelValueTrimmed(lbls.get(8)));
		Assert.assertEquals("Level 10 myvar=888 |", getLabelValueTrimmed(lbls.get(9)));
	}

	private String getLabelValueTrimmed(ComponentAgent agt) {
		return agt.as(Label.class).getValue().trim();
	}

	@Test
	public void testBindVar() throws Exception {
		DesktopAgent desktop = connect();

		Assert.assertEquals("111", getLabelValueTrimmed(desktop.query("#b1")));
		Assert.assertEquals("222", getLabelValueTrimmed(desktop.query("#b2")));
		Assert.assertEquals("333", getLabelValueTrimmed(desktop.query("#b3")));
		Assert.assertEquals("444", getLabelValueTrimmed(desktop.query("#b4")));
		Assert.assertEquals("555", getLabelValueTrimmed(desktop.query("#b5")));
		Assert.assertEquals("666", getLabelValueTrimmed(desktop.query("#b6")));
		Assert.assertEquals("777", getLabelValueTrimmed(desktop.query("#b7")));
		Assert.assertEquals("888", getLabelValueTrimmed(desktop.query("#b8")));
		Assert.assertEquals("888", getLabelValueTrimmed(desktop.query("#b9")));
		Assert.assertEquals("888", getLabelValueTrimmed(desktop.query("#b10")));
	}
}
