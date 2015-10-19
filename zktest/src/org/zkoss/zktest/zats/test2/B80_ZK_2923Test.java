/* B80_ZK_2923Test.java

	Purpose:
		
	Description:
		
	History:
		12:54 PM 10/19/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Textbox;

/**
 * @author jumperchen
 */
public class B80_ZK_2923Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		List<ComponentAgent> textboxes = desktop.queryAll("textbox");
		textboxes.get(0).type("abc");
		textboxes.get(1).type("abc2");

		desktop.query("button").click();

		assertEquals("abc", textboxes.get(0).as(Textbox.class).getValue());

		assertEquals("", textboxes.get(1).as(Textbox.class).getValue());
	}
}
