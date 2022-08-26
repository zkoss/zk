/* B96_ZK_5002Test.java

	Purpose:
		
	Description:
		
	History:
		5:39 PM 2021/11/5, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 */
public class B96_ZK_5002Test  extends ZATSTestCase {
	@Test
	public void test() throws Exception {
		DesktopAgent desktop = connect();
		ComponentAgent hostAgent = desktop.query("#host");
		ComponentAgent next = hostAgent.getFirstChild();
		assertEquals("RENDERED: myparam = value1, V=", next.getFirstChild().as(Label.class).getValue().trim());
		next = next.getNextSibling().getNextSibling();
		assertEquals("RENDERED: myparam = value2, V=", next.getFirstChild().as(Label.class).getValue().trim());
		next = next.getNextSibling().getNextSibling();
		assertEquals("RENDERED: myparam = value4, V=", next.getFirstChild().as(Label.class).getValue().trim());
		next = next.getNextSibling().getNextSibling();
		assertEquals("RENDERED: myparam = value5, V=true", next.getFirstChild().as(Label.class).getValue().trim());
		next = next.getNextSibling().getNextSibling();
		assertEquals("RENDERED: myparam = value6, V=", next.getFirstChild().as(Label.class).getValue().trim());
		next = next.getNextSibling().getNextSibling();
		assertEquals("RENDERED: myparam = value7, V=true", next.getFirstChild().as(Label.class).getValue().trim());
	}
}