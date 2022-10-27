/* IAudioTest.java

	Purpose:

	Description:

	History:
		Thu Oct 07 14:52:58 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zul.Audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link IAudio}
 *
 * @author katherine
 */
public class IAudioTest extends StatelessTestBase {
	@Test
	public void withAudio() {
		// check Richlet API case
		assertEquals(richlet(() -> IAudio.of("/media/guitar.wav")), zul(IAudioTest::newAudio));

		// check Stateless file case
		assertEquals(composer(IAudioTest::newAudio), zul(IAudioTest::newAudio));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> new Audio("/media/guitar2.wav"), (IAudio iAudio) -> iAudio.withSrc("/media/guitar.wav")),
				zul(IAudioTest::newAudio));
	}

	private static Audio newAudio() {
		return new Audio("/media/guitar.wav");
	}
}