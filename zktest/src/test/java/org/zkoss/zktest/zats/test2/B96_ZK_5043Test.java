/* B96_ZK_5043Test.java

	Purpose:
		
	Description:
		
	History:
		6:01 PM 2021/11/16, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 */
public class B96_ZK_5043Test  extends ZATSTestCase {
	@Test
	public void testMap() throws Exception {
		DesktopAgent desktop = connect();
		ComponentAgent mapAgent = desktop.query("#map");
		ComponentAgent replace1 = mapAgent.getFirstChild();;
		ComponentAgent change1 = replace1.getNextSibling();
		ComponentAgent value1 = change1.getNextSibling().getLastChild();
		String value = value1.as(Label.class).getValue();
		change1.click();
		assertNotEquals(value, value = value1.as(Label.class).getValue());

		replace1.click();
		assertNotEquals(value, value = value1.as(Label.class).getValue());

		change1.click();
		assertNotEquals(value, value1.as(Label.class).getValue());
	}
	@Test
	public void testDot() throws Exception {
		DesktopAgent desktop = connect();
		ComponentAgent dotAgent = desktop.query("#dot");
		ComponentAgent replace1 = dotAgent.getFirstChild();;
		ComponentAgent change1 = replace1.getNextSibling();
		ComponentAgent value1 = change1.getNextSibling().getLastChild();
		String value = value1.as(Label.class).getValue();
		change1.click();
		assertNotEquals(value, value = value1.as(Label.class).getValue());

		replace1.click();
		assertNotEquals(value, value = value1.as(Label.class).getValue());

		change1.click();
		assertNotEquals(value, value1.as(Label.class).getValue());
	}
}