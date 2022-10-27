/* IATest.java

	Purpose:
		
	Description:
		
	History:
		Wed Oct 06 09:53:17 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.A;

public class IATest extends StatelessTestBase {
	@Test
	public void withA() {
		// check Richlet API case
		assertEquals(richlet(() -> IA.of("abc").withHref("www.google.com")), zul(IATest::newA));

		// check Stateless file case
		assertEquals(composer(IATest::newA), zul(IATest::newA));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> new A("123"), (IA<IAnyGroup> iA) -> iA.withLabel("abc").withHref("www.google.com")),
				zul(IATest::newA));
	}

	@Test
	public void withDir() {
		try {
			IA.of("abc").withDir("dir");
		} catch (UiException ex) {
			assertEquals("getDir() should be 'normal' or 'reverse'", ex.getMessage());
		}
	}

	private static A newA() {
		A a = new A("abc");
		a.setHref("www.google.com");
		return a;
	}
}
