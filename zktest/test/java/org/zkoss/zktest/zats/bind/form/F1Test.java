/** F1Test.java.

	Purpose:
		
	Description:
		
	History:
		2:02:05 PM Dec 31, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.form;

import static org.junit.Assert.*;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 *
 */
public class F1Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent registerButton = desktop.query("#registerButton");
		ComponentAgent message = desktop.query("#message");
		final String msg = message.as(Label.class).getValue();
		registerButton.click();
		assertEquals(msg, message.as(Label.class).getValue());
		

		ComponentAgent accountBox = desktop.query("#accountBox");
		accountBox.type("john");
		ComponentAgent passwordBox = desktop.query("#passwordBox");
		passwordBox.type("1");
		ComponentAgent passwordBox2 = desktop.query("#passwordBox2");
		passwordBox2.type("2");
		registerButton.click();
		assertEquals(msg, message.as(Label.class).getValue());
		passwordBox2.type("1");
		registerButton.click();
		assertEquals("Hi, john: You are NOT an adult.", message.as(Label.class).getValue());
		
		desktop.query("#birthdayBox").type("1978/1/1");
		registerButton.click();
		assertEquals("Hi, john: You are an adult.", message.as(Label.class).getValue());
	}
}
