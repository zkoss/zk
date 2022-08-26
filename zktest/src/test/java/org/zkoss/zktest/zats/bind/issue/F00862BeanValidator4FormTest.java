/* F00862BeanValidator4FormTest.java
	Purpose:

	Description:

	History:
		Fri Apr 30 18:12:24 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class F00862BeanValidator4FormTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		ComponentAgent tb1 = desktopAgent.query("#tb1");
		tb1.input("");
		ComponentAgent msg1 = desktopAgent.query("#msg1");
		assertEquals("name can not be null", msg1.as(Label.class).getValue());
		tb1.input("a");
		assertEquals("", msg1.as(Label.class).getValue());

		ComponentAgent tb2 = desktopAgent.query("#tb2");
		tb2.input("");
		ComponentAgent msg2 = desktopAgent.query("#msg2");
		assertEquals("Last name can not be null", msg2.as(Label.class).getValue());
		tb2.input("b");
		assertEquals("", msg2.as(Label.class).getValue());

		ComponentAgent tb3 = desktopAgent.query("#tb3");
		tb3.input("1");
		ComponentAgent msg3 = desktopAgent.query("#msg3");
		assertEquals("not a well-formed email address", msg3.as(Label.class).getValue());
		tb3.input("111@111");
		assertEquals("email length must large than 8", msg3.as(Label.class).getValue());
		tb3.input("111@11111");
		assertEquals("", msg3.as(Label.class).getValue());
	}
}