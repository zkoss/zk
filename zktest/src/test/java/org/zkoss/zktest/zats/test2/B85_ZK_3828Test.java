/* B85_ZK_3828Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 28 09:51:22 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

/**
 * @author rudyhuang
 */
public class B85_ZK_3828Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent dt = connect();
		assertEquals("remove 'error' from this text", getTextbox(dt).as(Textbox.class).getValue());
		assertEquals("ERROR", getStatus(dt).as(Label.class).getValue());
		assertEquals("false", getFlag(dt).as(Label.class).getValue());

		getTextbox(dt).input("correct value");
		assertEquals("OK", getStatus(dt).as(Label.class).getValue());
		assertEquals("true", getFlag(dt).as(Label.class).getValue());
	}

	private ComponentAgent getTextbox(DesktopAgent dt) {
		return dt.query("div > textbox");
	}

	private ComponentAgent getStatus(DesktopAgent dt) {
		return dt.queryAll("div > label").get(0);
	}

	private ComponentAgent getFlag(DesktopAgent dt) {
		return dt.queryAll("div > label").get(1);
	}
}
