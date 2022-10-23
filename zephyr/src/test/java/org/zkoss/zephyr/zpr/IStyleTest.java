/* IStyleTest.java

	Purpose:

	Description:

	History:
		Wed Nov 03 15:57:21 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.junit.jupiter.api.Test;
import org.zkoss.zephyr.mock.ZephyrTestBase;
import org.zkoss.zul.Style;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link IStyle}
 *
 * @author katherine
 */
public class IStyleTest extends ZephyrTestBase {
	@Test
	public void withStyle() {
		// check Richlet API case
		assertEquals(richlet(() -> IStyle.ofSrc("abc")), zul(IStyleTest::newStyle));

		// check Zephyr file case
		assertEquals(composer(IStyleTest::newStyle), zul(IStyleTest::newStyle));

		// check Zephyr file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> new Style("123"), (IStyle iStyle) -> iStyle.withSrc("abc")),
				zul(IStyleTest::newStyle));
	}

	private static Style newStyle() {
		return new Style("abc");
	}
}