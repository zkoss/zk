/* IProgressmeterTest.java

	Purpose:

	Description:

	History:
		Thu Dec 16 11:52:41 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.zkoss.zephyr.mock.ZephyrTestBase;
import org.zkoss.zul.Progressmeter;

/**
 * Test for {@link IProgressmeter}
 *
 * @author katherine
 */
public class IProgressmeterTest extends ZephyrTestBase {
	@Test
	public void withProgressmeter() {
		// check Richlet API case
		assertEquals(richlet(() -> IProgressmeter.of(5)), zul(IProgressmeterTest::newProgressmeter));

		// check Zephyr file case
		assertEquals(composer(IProgressmeterTest::newProgressmeter), zul(IProgressmeterTest::newProgressmeter));

		// check Zephyr file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> new Progressmeter(10), (IProgressmeter iProgressmeter) -> iProgressmeter.withValue(5)),
				zul(IProgressmeterTest::newProgressmeter));
	}

	private static Progressmeter newProgressmeter() {
		return new Progressmeter(5);
	}
}