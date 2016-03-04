/* B80_ZK_3107Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 2 14:12:46 CST 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Layout;
import org.zkoss.zul.Textbox;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author jameschu
 */
public class B80_ZK_3107Test extends ZATSTestCase {
	@Test
	public void test() {
		try {
			DesktopAgent desktop = connect();
			ComponentAgent textbox = desktop.query("textbox");
			Textbox t = textbox.as(Textbox.class);
			assertEquals("Peter", t.getValue());
			ComponentAgent l1 = desktop.query("#l1");
			assertEquals("PETER", l1.as(Label.class).getValue());
			ComponentAgent l2 = desktop.query("#l2");
			assertEquals("5", l2.as(Label.class).getValue());
			ComponentAgent l3 = desktop.query("#l3");
			assertEquals("3", l3.as(Label.class).getValue());
			ComponentAgent l4 = desktop.query("#l4");
			assertEquals("1", l4.as(Label.class).getValue());

			desktop.query("button").click();
			assertEquals("HI", l1.as(Label.class).getValue());
			assertEquals("2", l2.as(Label.class).getValue());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
