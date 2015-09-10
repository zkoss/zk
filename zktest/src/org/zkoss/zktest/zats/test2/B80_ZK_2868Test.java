/* B80_ZK_2868Test.java

	Purpose:
		
	Description:
		
	History:
		12:35 PM 9/10/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 */
public class B80_ZK_2868Test extends ZATSTestCase {
	@Test public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent main = desktop.query("#main");
		boolean hasChild = false;
		String[] messages = {"telefon", "address", "email"};
		int i = 0;
		for (ComponentAgent row : main.getFirstChild().queryAll("row")) {
			assertEquals(2, row.getChildren().size());
			assertEquals(messages[i], row.getFirstChild().as(Label.class).getValue().trim());
			hasChild = true;
			i++;
		}

		assertTrue(hasChild);

		hasChild = false;
		i = 0;
		for (ComponentAgent row : main.getFirstChild().getNextSibling().queryAll(
				"row")) {
			assertEquals(2, row.getChildren().size());
			assertEquals(messages[i], row.getFirstChild().as(Label.class).getValue().trim());
			hasChild = true;
			i++;
		}

		assertTrue(hasChild);
	}
}