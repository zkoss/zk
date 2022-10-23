/* IIframeTest.java

	Purpose:

	Description:

	History:
		Wed Nov 03 15:06:03 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.junit.jupiter.api.Test;
import org.zkoss.zephyr.mock.ZephyrTestBase;
import org.zkoss.zul.Iframe;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link IIframe}
 *
 * @author katherine
 */
public class IIframeTest extends ZephyrTestBase {
	@Test
	public void withIframe() {
		// check Richlet API case
		assertEquals(richlet(() -> IIframe.of("abc")), zul(IIframeTest::newIframe));

		// check Zephyr file case
		assertEquals(composer(IIframeTest::newIframe), zul(IIframeTest::newIframe));

		// check Zephyr file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> new Iframe("123"), (IIframe iIframe) -> iIframe.withSrc("abc")),
				zul(IIframeTest::newIframe));
	}

	private static Iframe newIframe() {
		return new Iframe("abc");
	}
}