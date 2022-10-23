/* IAreaTest.java

	Purpose:

	Description:

	History:
		Thu Oct 07 14:52:23 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.junit.jupiter.api.Test;
import org.zkoss.zephyr.mock.ZephyrTestBase;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.Area;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link IArea}
 *
 * @author katherine
 */
public class IAreaTest extends ZephyrTestBase {
	@Test
	public void withArea() {
		// check Richlet API case
		assertEquals(richlet(() -> IArea.of("0,0,0,0")), zul(IAreaTest::newArea));

		// check Zephyr file case
		assertEquals(composer(IAreaTest::newArea), zul(IAreaTest::newArea));

		// check Zephyr file and then recreate another immutable case
		assertEquals(
			thenComposer(() -> {
				Area area = new Area();
				area.setCoords("0,0,0,1");
				return(area);
			}, (IArea iArea) -> iArea.withCoords("0,0,0,0")),
			zul(IAreaTest::newArea)
		);
	}

	@Test
	public void withShape() {
		String shape = "abc";
		try {
			IArea.of("abc").withShape(shape);
		} catch (UiException ex) {
			assertEquals("Unknown shape: " + shape, ex.getMessage());
		}
	}

	private static Area newArea() {
		Area area = new Area("0,0,0,0");
		return area;
	}
}