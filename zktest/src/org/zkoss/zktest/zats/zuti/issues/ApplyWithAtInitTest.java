/** ApplyWithAtInitTest.java.

	Purpose:
		
	Description:
		
	History:
		3:17:26 PM Feb 9, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.issues;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

/**
 * @author jumperchen
 *
 */
public class ApplyWithAtInitTest extends ZutiBasicTestCase {
	@Test
	public void testResult() {
		
		try {
			DesktopAgent desktop = connect();
			List<ComponentAgent> textboxes = desktop.queryAll("textbox");
			assertEquals(3, textboxes.size());
			for (ComponentAgent next : textboxes) {
				assertEquals("old Name", next.as(Textbox.class).getValue());
				assertEquals("old Name", next.getNextSibling().as(Label.class).getValue());
			}

			textboxes.get(1).type("Test");
			
			for (ComponentAgent next : textboxes) {
				assertEquals("Test", next.as(Textbox.class).getValue());
				assertEquals("Test", next.getNextSibling().as(Label.class).getValue());
			}
		} catch (Exception e) {
			fail();
		}
	}
}
