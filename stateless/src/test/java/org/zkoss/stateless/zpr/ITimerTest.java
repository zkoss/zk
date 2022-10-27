/* ITimerTest.java

	Purpose:

	Description:

	History:
		Thu Nov 04 18:06:42 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zul.Timer;

/**
 * Test for {@link ITimer}
 *
 * @author katherine
 */
public class ITimerTest extends StatelessTestBase {
	@Test
	public void withTimer() {
		// check Richlet API case
		assertEquals(richlet(() -> ITimer.ofDelay(10)), zul(ITimerTest::newTimer));

		// check Stateless file case
		assertEquals(composer(ITimerTest::newTimer), zul(ITimerTest::newTimer));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> new Timer(5), (ITimer iTimer) -> iTimer.withDelay(10)),
				zul(ITimerTest::newTimer));
	}

	private static Timer newTimer() {
		return new Timer(10);
	}
}