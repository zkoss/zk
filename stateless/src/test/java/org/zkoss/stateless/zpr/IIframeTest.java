/* IIframeTest.java

	Purpose:

	Description:

	History:
		Wed Nov 03 15:06:03 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zul.Iframe;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link IIframe}
 *
 * @author katherine
 */
public class IIframeTest extends StatelessTestBase {
	@Test
	public void withIframe() {
		// check Richlet API case
		assertEquals(richlet(() -> IIframe.of("abc")), zul(IIframeTest::newIframe));

		// check Stateless file case
		assertEquals(composer(IIframeTest::newIframe), zul(IIframeTest::newIframe));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> new Iframe("123"), (IIframe iIframe) -> iIframe.withSrc("abc")),
				zul(IIframeTest::newIframe));
	}

	private static Iframe newIframe() {
		return new Iframe("abc");
	}
}