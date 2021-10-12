/* B80_ZK_3136Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 31 16:01:32 CST 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Textbox;

import java.util.List;

/**
 * @author jameschu
 */
public class B80_ZK_3149Test extends ZATSTestCase {
	@Test public void test() {
		connect();
		DesktopAgent desktop = connect();
		List<ComponentAgent> textboxes = desktop.queryAll("textbox");
		List<ComponentAgent> buttons = desktop.queryAll("button");
		assertEquals("handled", textboxes.get(1).as(Textbox.class).getValue());
		textboxes.get(0).type("aaa");
		buttons.get(1).click();
		assertEquals("Peter", textboxes.get(0).as(Textbox.class).getValue());
	}
}
