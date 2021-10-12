/* B80_ZK_3009Test.java

	Purpose:
		
	Description:
		
	History:
		6:25 PM 12/11/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 */
public class B80_ZK_3009Test extends ZATSTestCase {

	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent div = desktop.query("div");
		List<ComponentAgent> label = div.queryAll("label");
		assertEquals("TEST", label.get(0).as(Label.class).getValue());
		assertEquals("TEST2", label.get(1).as(Label.class).getValue());
	}
}
