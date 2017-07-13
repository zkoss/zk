/* F85_ZK_3684.java

	Purpose:

	Description:

	History:
		Mon Jun 12 11:35:07 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class F85_ZK_3684Test extends ZATSTestCase {
	@Test
	public void testSize() throws Exception {
		DesktopAgent desktop = connect();
		desktop.query("textbox").type("");
		assertTrue(
				"Validation message should not be empty.",
				desktop.query("label").as(Label.class).getValue().length() > 0
		);
	}

	@Test
	public void testPattern() throws Exception {
		DesktopAgent desktop = connect();
		desktop.query("textbox").type("11957");
		assertTrue(
				"Validation message should not be empty.",
				desktop.query("label").as(Label.class).getValue().length() > 0
		);
	}

	@Test
	public void testNormal() throws Exception {
		DesktopAgent desktop = connect();
		desktop.query("textbox").type("Albert");
		assertEquals(
				"Validation message should be empty.",
				"",
				desktop.query("label").as(Label.class).getValue()
		);
	}
}
