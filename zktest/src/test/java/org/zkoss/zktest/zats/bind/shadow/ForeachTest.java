/* ForeachTest.java

	Purpose:
		
	Description:
		
	History:
		Thu May 06 15:24:34 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.shadow;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.ZatsException;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class ForeachTest extends ZATSTestCase {
	@Test
	public void testItems() {
		final DesktopAgent desktop = connect("/bind/shadow/foreach-items.zul");
		final List<ComponentAgent> navitems = desktop.queryAll("#navbar > navitem");
		Assertions.assertEquals(6, navitems.size());
	}

	@Test
	public void testNumbers() {
		final DesktopAgent desktop = connect("/bind/shadow/foreach-numbers.zul");
		final ComponentAgent multiplicationTable = desktop.query("#multiplicationTable");
		Assertions.assertEquals(5, multiplicationTable.queryAll("vlayout").size());
		Assertions.assertEquals(50, multiplicationTable.queryAll("label").size());
	}

	@Test
	public void testNumbersStepMinus() {
		Throwable t = Assertions.assertThrows(ZatsException.class, () -> {
			connect("/bind/shadow/foreach-numbers-step-minus.zul");
		});
		Assertions.assertEquals(IllegalArgumentException.class, t.getCause().getClass());
	}

	@Test
	public void testNumbersReverse() {
		final DesktopAgent desktop = connect("/bind/shadow/foreach-numbers-reverse.zul");
		final ComponentAgent multiplicationTable = desktop.query("#multiplicationTable");
		Assertions.assertEquals(5, multiplicationTable.queryAll("vlayout").size());
		Assertions.assertEquals(0, multiplicationTable.queryAll("label").size());
	}
}
